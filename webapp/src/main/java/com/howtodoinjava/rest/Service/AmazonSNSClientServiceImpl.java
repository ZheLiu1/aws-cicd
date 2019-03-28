package com.howtodoinjava.rest.Service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.howtodoinjava.rest.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AmazonSNSClientServiceImpl implements AmazonSNSClientService{
    private AmazonSNS amazonSNS;
    private String snsTopicInterviewStatusARN;
    private static final Logger logger = LoggerFactory.getLogger(AmazonSNSClientServiceImpl.class);

    @Autowired
    public AmazonSNSClientServiceImpl(AWSCredentialsProvider awsCredentialsProvider, String snsTopicInterviewStatusARN) {
        this.amazonSNS = AmazonSNSClientBuilder.standard().withCredentials(awsCredentialsProvider).build();
        this.snsTopicInterviewStatusARN = snsTopicInterviewStatusARN;
    }

    @Override
    public void publish(String message, String topic){
        //
        // Get Appropriate Topic ARN
        //
        String snsTopic = getTopicARN(topic);
        //
        // Create Publish Message Request with TopicARN and Message
        //
        PublishRequest publishRequest = new PublishRequest(snsTopic, message);
        //
        // Publish the message
        //
        PublishResult publishResult = this.amazonSNS.publish(publishRequest);
        //
        // Evaluate the result: Print MessageId of message published to SNS topic
        //
        logger.info("MessageId - " + publishResult.getMessageId());

    }

    private String getTopicARN(String topic) {
        switch(topic) {
            case TOPIC_INTERVIEWSTATUS:
                return this.snsTopicInterviewStatusARN;
            default: {
                logger.error("No matching topic supported!");
                return null;
            }
        }
    }
}
