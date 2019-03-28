package com.howtodoinjava.rest.Service;

public interface AmazonSNSClientService {
    //
    // Name of the topic
    //
    public static final String TOPIC_INTERVIEWSTATUS = "SNSReset";
    //
    // Publish Message API
    //
    void publish(String message, String topic);
}
