package com.howtodoinjava.rest.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.s3.audio.bucket}")
    private String awsS3AudioBucket;

    @Value("${aws.sns.topic.ARN}")
    private String snsTopicInterviewStatusARN;


    @Bean(name = "snsTopicInterviewStatusARN")
    public String snsTopcARN() {
        return this.snsTopicInterviewStatusARN;
    }

    @Bean(name = "awsRegion")
    public Region getAWSPollyRegion() {
        return Region.getRegion(Regions.fromName(awsRegion));
    }

    @Bean(name = "awsCredentialsProvider")
    public AWSCredentialsProvider getAWSCredentials() {
        return new DefaultAWSCredentialsProviderChain();
    }

    @Bean(name = "awsS3AudioBucket")
    public String getAWSS3AudioBucket() {
        return awsS3AudioBucket;
    }
}
