package com.reliefcircle.util.aws;

public enum BucketName {
    PROFILE_IMAGE("reliefcircle-test");
    private final String bucketName;
    BucketName(String bucketName)
    {
        this.bucketName=bucketName;
    }
    public String getBucketName() {
        return bucketName;
    }

}
