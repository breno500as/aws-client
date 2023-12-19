package com.br.aws.client.api;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.br.aws.client.dto.UrlResponseDTO;
import com.br.aws.client.entity.InvoiceEntity;
import com.br.aws.client.repository.InvoiceRepository;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceAPI {

	@Value("${aws.s3.bucket.invoice.name}")
	private String bucketName;

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private InvoiceRepository invoiceRepository;

	@PostMapping
	public ResponseEntity<UrlResponseDTO> createInvoiceUrl() {

		final UrlResponseDTO urlResponseDTO = new UrlResponseDTO();
		Instant instant = Instant.now().plus(Duration.ofMinutes(5));

		String processId = UUID.randomUUID().toString();

		final GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(this.bucketName,
				processId).withMethod(HttpMethod.PUT).withExpiration(Date.from(instant));

		urlResponseDTO.setExpirationTime(instant.getEpochSecond());

		urlResponseDTO.setUrl(this.amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString());

		return ResponseEntity.ok(urlResponseDTO);

	}

	@GetMapping
	public ResponseEntity<Iterable<InvoiceEntity>> findAll(@RequestParam(name = "customerName", required = false) String custumerName) {

		if (ObjectUtils.isEmpty(custumerName)) {
			return ResponseEntity.ok().body(this.invoiceRepository.findAll());

		}

		return ResponseEntity.ok().body(this.invoiceRepository.findAllByCustomerName(custumerName));
	}

}
