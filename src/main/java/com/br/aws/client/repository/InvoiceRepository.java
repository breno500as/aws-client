package com.br.aws.client.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.br.aws.client.entity.InvoiceEntity;

public interface InvoiceRepository extends CrudRepository<InvoiceEntity, Long> {
	
	Optional<InvoiceEntity> findByInvoiceNumber(String number);
	
	List<InvoiceEntity> findAllByCustomerName(String customerName);

 
}
