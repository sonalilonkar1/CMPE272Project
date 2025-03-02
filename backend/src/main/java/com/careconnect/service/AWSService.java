package com.careconnect.service;

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
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.careconnect.dto.CharityDto;

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

    public AWSService(@Value("${aws.accessKeyId}") String awsAccessKeyId,
                      @Value("${aws.secretKey}") String awsSecret) {

        BasicAWSCredentials credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecret);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1) // Change as needed
                .build();

        this.snsClient = AmazonSNSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    public boolean uploadFile(String keyName, MultipartFile file) {
      int retryCount = 3;
      while (retryCount > 0) {
          try (InputStream inputStream = file.getInputStream()) {
              ObjectMetadata metadata = new ObjectMetadata();
              metadata.setContentLength(file.getSize());
              s3Client.putObject(new PutObjectRequest(primaryBucket, keyName, inputStream, metadata));
              log.info("Successfully uploaded file: {}", keyName);
              return true;
          } catch (IOException | AmazonServiceException e) {
              retryCount--;
              log.error("Upload failed (attempt {}): {}", 4 - retryCount, e.getMessage(), e);
              if (retryCount == 0) {
                  return false; // After max retries, return false
              }
              // Optionally add delay between retries
              try {
                  Thread.sleep(1000); // sleep for 1 second before retry
              } catch (InterruptedException ie) {
                  Thread.currentThread().interrupt(); // handle interruption
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