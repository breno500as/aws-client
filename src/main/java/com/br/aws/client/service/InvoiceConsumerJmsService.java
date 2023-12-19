package com.br.aws.client.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.model.S3Object;
import com.br.aws.client.dto.SnsMessageDTO;
import com.br.aws.client.entity.InvoiceEntity;
import com.br.aws.client.repository.InvoiceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class InvoiceConsumerJmsService {

	private static final Logger log = LoggerFactory.getLogger(InvoiceConsumerJmsService.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private AmazonS3 amazonS3;

	@JmsListener(destination = "${aws.sqs.queue.invoice.name}")
	public void receiveProductEvent(TextMessage textMessage) throws JMSException, IOException {

		final SnsMessageDTO snsMessage = this.objectMapper.readValue(textMessage.getText(), SnsMessageDTO.class);

		final S3EventNotification s3EventNotification = this.objectMapper.readValue(snsMessage.getMessage(),
				S3EventNotification.class);

		this.processInvoiceNotification(s3EventNotification);

	}

	private void processInvoiceNotification(final S3EventNotification s3EventNotification) {

		if (s3EventNotification.getRecords() != null) {

			s3EventNotification.getRecords().forEach(r -> {
				final S3Entity s3Entity = r.getS3();

				final String bucketName = s3Entity.getBucket().getName();
				final String objectKey = s3Entity.getObject().getKey();

				final String invoiceObject = downloadObject(bucketName, objectKey);

				InvoiceEntity invoiceEntity = null;

				try {

					invoiceEntity = this.objectMapper.readValue(invoiceObject, InvoiceEntity.class);

				} catch (Exception e) {
					log.error("Erro ao fazer o parsing da invoice: {}", e.getMessage(), e);
					throw new RuntimeException("Erro ao fazer o parsing da invoice");
				}

				log.info("Invoice received: {}", invoiceEntity.getInvoiceNumber());

				this.invoiceRepository.save(invoiceEntity);

				this.amazonS3.deleteObject(bucketName, objectKey);
			});

		}

	}

	private String downloadObject(String bucketName, String objectKey) {

		try {
			S3Object s3Object = amazonS3.getObject(bucketName, objectKey);

			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
			String content = null;
			while ((content = bufferedReader.readLine()) != null) {
				stringBuilder.append(content);
			}
			return stringBuilder.toString();
		} catch (Exception e) {
			log.error("Erro ao recuperar objeto do bucket: {}", e.getMessage(), e);
			throw new RuntimeException("Erro ao recuperar objeto do bucket");
		}
	}

}
