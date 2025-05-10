package com.reliefcircle.util.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.ResponseBytes;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Service
public class FileStore {
    private final S3Client s3Client;
    
    @Autowired
    public FileStore(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    
    public void save(String path, String fileName, Optional<Map<String, String>> optionalMetadata, InputStream inputStream) {
        try {
            PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                .bucket(path)
                .key(fileName);
            
            // Convert metadata to a format compatible with AWS SDK v2
            Map<String, String> metadata = new HashMap<>();
            optionalMetadata.ifPresent(map -> {
                if (!map.isEmpty()) {
                    metadata.putAll(map);
                }
            });
            
            s3Client.putObject(requestBuilder.build(), RequestBody.fromInputStream(inputStream, -1));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to store file to S3", e);
        }
    }

    public byte[] download(String path, String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                .bucket(path)
                .key(key)
                .build();
            
            ResponseBytes<?> responseBytes = s3Client.getObjectAsBytes(request);
            return responseBytes.asByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to download from S3", e);
        }
    }
}
