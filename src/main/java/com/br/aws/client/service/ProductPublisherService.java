package com.br.aws.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.Topic;
import com.br.aws.client.config.AwsLocalStackSns;
import com.br.aws.client.dto.EnvelopeDTO;
import com.br.aws.client.dto.ProductEventDTO;
import com.br.aws.client.entity.ProductEntity;
import com.br.aws.client.enuns.EventTypeEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductPublisherService {
	
	private static final Logger LOG = LoggerFactory.getLogger(AwsLocalStackSns.class);

	@Autowired
	private AmazonSNS snsClient;

	@Autowired
	@Qualifier("productTopic")
	private Topic productTopic;

	@Autowired
	private ObjectMapper objectMapper;

	public void publishProductEvent(ProductEntity product, EventTypeEnum eventType, String username) {
		ProductEventDTO productEvent = new ProductEventDTO();
		productEvent.setProductId(product.getId());
		productEvent.setCode(product.getCode());
		productEvent.setUsername(username);

		EnvelopeDTO envelope = new EnvelopeDTO();
		envelope.setEventType(eventType);

		try {
			envelope.setData(objectMapper.writeValueAsString(productEvent));

			this.snsClient.publish(this.productTopic.getTopicArn(), objectMapper.writeValueAsString(envelope));

		} catch (JsonProcessingException e) {
			LOG.error("Failed to create product event message");
		}
	}

}
