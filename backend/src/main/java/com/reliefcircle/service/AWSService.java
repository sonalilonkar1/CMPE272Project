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
    private final String primaryBucket;
    private final String snsTopic;
    private final String region;

    public AWSService(
            @Value("${aws.accessKeyId}") String awsAccessKeyId,
            @Value("${aws.secretKey}") String awsSecret,
            @Value("${aws.region:us-east-2}") String region,
            @Value("${aws.s3.bucket-name}") String primaryBucket,
            @Value("${sns.topic.arn}") String snsTopic
    ) {
        this.primaryBucket = primaryBucket;
        this.snsTopic = snsTopic;
        this.region = region;

        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsAccessKeyId, awsSecret);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();

        this.snsClient = SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();

        log.info("Bucket Name: {}", primaryBucket);
    }

    public String uploadProofDocument(MultipartFile file, String folder) {
        String fileName = folder + "/" + java.util.UUID.randomUUID() + "-" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(primaryBucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, file.getSize())
            );

            log.info("Successfully uploaded file: {}", fileName);

            return "https://" + primaryBucket + ".s3." + region + ".amazonaws.com/" + fileName;
        } catch (IOException e) {
            log.error("Failed to upload file to S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    
    /**
     * Publishes a message to the SNS topic for charity registration
     *
     * @param charityDto Charity information to be sent
     * @return true if the message was published successfully, false otherwise
     */
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
}
