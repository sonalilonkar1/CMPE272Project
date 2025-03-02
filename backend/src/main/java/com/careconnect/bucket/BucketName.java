package com.careconnect.bucket;

public enum BucketName {
    PROFILE_IMAGE("careconnect-test");
    private final String bucketName;
    BucketName(String bucketName)
    {
        this.bucketName=bucketName;
    }
    public String getBucketName() {
        return bucketName;
    }

}
