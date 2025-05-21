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
import software.amazon.awssdk.services.sns.model.PublishResponse;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Getter
public class AWSService {
    private final S3Client s3Client;
    private final SnsClient snsClient;
    private final String primaryBucket;
    private final String snsTopicCharity;
    private final String snsTopicUpdates;
    private final String region;

    public AWSService(
            @Value("${aws.accessKeyId}") String awsAccessKeyId,
            @Value("${aws.secretKey}") String awsSecret,
            @Value("${aws.region:us-east-2}") String region,
            @Value("${aws.s3.bucket-name}") String primaryBucket,
            @Value("${sns.topic.arn.charity}") String snsTopicCharity,
            @Value("${sns.topic.arn.updates}") String snsTopicUpdates
    ) {
        this.primaryBucket = primaryBucket;
        this.snsTopicCharity = snsTopicCharity;
        this.snsTopicUpdates = snsTopicUpdates;
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
     * Sends a notification to the specified recipient using SNS.
     *
     * @param subject   The subject of the notification.
     * @param message   The message body of the notification.
     * @param snsTopic  The SNS topic ARN to publish the message to.
     */
    public void sendNotification(String subject, String message, String topic) {
        String snsTopic;
        if(topic.equals("charity")) {
            snsTopic = snsTopicCharity;
        } else if(topic.equals("updates")) {
            snsTopic = snsTopicUpdates;
        } else {
            throw new IllegalArgumentException("Invalid SNS topic specified");
        }
        try {
            PublishRequest request = PublishRequest.builder()
                    .topicArn(snsTopic)
                    .subject(subject)
                    .message(message)
                    .build();
            PublishResponse result = snsClient.publish(request);
            log.info("SNS Email sent for {}. Message ID: {}", topic ,result.messageId());
        } catch (Exception e) { 
            log.error("SNS Publish failed: {}", e.getMessage(), e);
        }
    }

    /**
     * Subscribes a user's email to a specific SNS topic.
     *
     * @param userEmail The email address of the user to subscribe.
     * @param topic     The SNS topic to subscribe the user to.
     */
    public void subscribeEmailToTopic(String userEmail, String topic) {
        String snsTopic;
        if(topic.equals("charity")) {
            snsTopic = snsTopicCharity;
        } else if(topic.equals("updates")) {
            snsTopic = snsTopicUpdates;
        } else {
            throw new IllegalArgumentException("Invalid SNS topic specified");
        }

        try {
            var subscribeRequest = software.amazon.awssdk.services.sns.model.SubscribeRequest.builder()
                    .topicArn(snsTopic)
                    .protocol("email")
                    .endpoint(userEmail)
                    .build();

            var response = snsClient.subscribe(subscribeRequest);
            log.info("Subscription request sent to {}. Subscription ARN: {}", userEmail, response.subscriptionArn());
        } catch (Exception e) {
            log.error("Failed to subscribe email to SNS topic: {}", e.getMessage(), e);
            throw new RuntimeException("SNS subscription failed", e);
        }
    }

}
