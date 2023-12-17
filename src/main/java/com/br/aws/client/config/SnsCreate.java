package com.br.aws.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.Topic;

@Configuration
@Profile("localhost")
public class SnsCreate {

	private static final Logger LOG = LoggerFactory.getLogger(SnsCreate.class);
	private final String productTopic;
	private final AmazonSNS snsClient;

	public SnsCreate() {
		this.snsClient = AmazonSNSClient.builder().withEndpointConfiguration(
				new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", Regions.US_EAST_1.getName()))
				.withCredentials(new DefaultAWSCredentialsProviderChain()).build();

		CreateTopicRequest createTopicRequest = new CreateTopicRequest("product-topic");
		this.productTopic = this.snsClient.createTopic(createTopicRequest).getTopicArn();
		LOG.info("SNS topic ARN: {}", this.productTopic);
	}

	@Bean
	public AmazonSNS snsClient() {
		return this.snsClient;
	}

	@Bean(name = "productTopic")
	public Topic snsProductTopic() {
		return new Topic().withTopicArn(productTopic);
	}

}
