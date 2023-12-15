package br.com.aws.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.Topic;

@Configuration
@Profile("!localhost")
public class SnsConfig {

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.sns.topic.product.arn}")
	private String productTopic;

	@Bean
	public AmazonSNS snsClient() {
		return AmazonSNSClientBuilder
				.standard()
				.withRegion(this.awsRegion)
				.withCredentials(new DefaultAWSCredentialsProviderChain()).build();
	}

	@Bean(name = "productTopic")
	public Topic snsProductTopic() {
		return new Topic().withTopicArn(this.productTopic);
	}
}
