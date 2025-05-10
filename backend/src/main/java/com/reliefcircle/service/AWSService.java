package com.reliefcircle.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import com.reliefcircle.dto.CharityDto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Getter
public class AWSService {
    private final S3Client s3Client;
    private final SnsClient snsClient;
    
    @Value("${cloudfront.url}")
    private String cloudFrontUrl;
    
    @Value("${primary.bucket}")
    private String primaryBucket;
    
    @Value("${sns.topic.arn}")
    private String snsTopic;
    
    public AWSService(@Value("${aws.accessKeyId}") String awsAccessKeyId,
                      @Value("${aws.secretKey}") String awsSecret,
                      @Value("${aws.region:us-east-1}") String region) {
        // Create credentials provider
        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsAccessKeyId, awsSecret);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        
        // Create S3 client
        this.s3Client = S3Client.builder()
                .region(Region.of(region.toUpperCase()))
                .credentialsProvider(credentialsProvider)
                .build();
        
        // Create SNS client
        this.snsClient = SnsClient.builder()
                .region(Region.of(region.toUpperCase()))
                .credentialsProvider(credentialsProvider)
                .build();
    }
    
    /**
     * Uploads a file to S3 with the provided key name
     * @param keyName The key name for the file in S3
     * @param file The file to upload
     * @return true if the upload was successful, false otherwise
     */
    public boolean uploadFile(String keyName, MultipartFile file) {
        final int MAX_RETRIES = 3;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try (InputStream inputStream = file.getInputStream()) {
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(primaryBucket)
                    .key(keyName)
                    .contentType(file.getContentType())
                    .build();
                
                s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, file.getSize()));
                log.info("Successfully uploaded file: {}", keyName);
                return true;
            } catch (IOException e) {
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
    
    /**
     * Uploads a file to S3 and returns the URL for the uploaded file
     * @param file The file to upload
     * @param folder The folder path in S3
     * @return The URL of the uploaded file
     */
    public String uploadProofDocument(MultipartFile file, String folder) {
        String fileName = folder + "/" + java.util.UUID.randomUUID() + "-" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(primaryBucket)
                .key(fileName)
                .contentType(file.getContentType())
                .build();
            
            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, file.getSize()));
            log.info("Successfully uploaded file: {}", fileName);
            
            return "https://" + primaryBucket + ".s3.amazonaws.com/" + fileName;
        } catch (IOException e) {
            log.error("Failed to upload file to S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }
    
    public boolean pubMessageToFundraiser(CharityDto charityDto) {
        try {
            PublishRequest request = PublishRequest.builder()
                    .message("Charity Info: " + charityDto.toString())
                    .topicArn(snsTopic)
                    .subject("Charity Registration: " + charityDto.getName())
                    .build();
            
            var result = snsClient.publish(request);
            log.info("SNS Message sent. Message ID: {}", result.messageId());
            return true;
        } catch (Exception e) {
            log.error("SNS Publish failed: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Alias for backward compatibility
     */
    public boolean pubMessageToAdmin(CharityDto charityDto) {
        return pubMessageToFundraiser(charityDto);
    }
}