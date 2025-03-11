package com.reliefcircle.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.reliefcircle.dto.CharityDto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Getter
public class AWSService {
    private final AmazonS3 s3Client;
    private final AmazonSNS snsClient;
    
    @Value("${cloudfront.url}")
    private String cloudFrontUrl;
    
    @Value("${primary.bucket}")
    private String primaryBucket;
    
    @Value("${sns.topic.arn}")
    private String snsTopic;
    
    public AWSService(AmazonS3 s3Client, @Value("${aws.accessKeyId}") String awsAccessKeyId,
                      @Value("${aws.secretKey}") String awsSecret,
                      @Value("${aws.region:us-east-1}") String region) {
        // Use the s3Client from AmazonConfig
        this.s3Client = s3Client;
        
        // Create the SNS client
        BasicAWSCredentials credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecret);
        this.snsClient = AmazonSNSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.fromName(region))
                .build();
    }
    
    public boolean uploadFile(String keyName, MultipartFile file) {
        final int MAX_RETRIES = 3;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try (InputStream inputStream = file.getInputStream()) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                metadata.setContentType(file.getContentType());
                
                s3Client.putObject(new PutObjectRequest(primaryBucket, keyName, inputStream, metadata));
                log.info("Successfully uploaded file: {}", keyName);
                return true;
            } catch (IOException | AmazonServiceException e) {
                log.error("Upload failed (attempt {}/{}): {}", attempt, MAX_RETRIES, e.getMessage(), e);
                
                if (attempt == MAX_RETRIES) {
                    return false;
                }
                
                try {
                    Thread.sleep(1000 * attempt); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return false;
    }
    
    public boolean pubMessageToAdmin(CharityDto charityDto) {
        try {
            PublishRequest request = new PublishRequest()
                    .withMessage("Charity Info: " + charityDto.toString())
                    .withTopicArn(snsTopic)
                    .withSubject("Charity Registration: " + charityDto.getCname());
            
            PublishResult result = snsClient.publish(request);
            log.info("SNS Message sent. Message ID: {}", result.getMessageId());
            return true;
        } catch (Exception e) {
            log.error("SNS Publish failed: {}", e.getMessage(), e);
            return false;
        }
    }
}