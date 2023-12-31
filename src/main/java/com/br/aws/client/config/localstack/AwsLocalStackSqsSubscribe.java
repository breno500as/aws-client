package com.br.aws.client.config.localstack;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.Topic;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;

@Configuration
@Profile("localhost")
public class AwsLocalStackSqsSubscribe {
	
	
	  public AwsLocalStackSqsSubscribe(AmazonSNS snsClient, @Qualifier("productTopic") Topic productTopic) {
	        AmazonSQS sqsClient = AmazonSQSClient.builder()
	                .withEndpointConfiguration(
	                        new AwsClientBuilder.EndpointConfiguration("http://localhost:4566",
	                                Regions.US_EAST_1.getName()))
	                .withCredentials(new DefaultAWSCredentialsProviderChain())
	                .build();

	        String productQueueUrl = sqsClient.createQueue( new CreateQueueRequest("product-queue")).getQueueUrl();

	        Topics.subscribeQueue(snsClient, sqsClient, productTopic.getTopicArn(), productQueueUrl);
	    }

}
