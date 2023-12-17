package com.br.aws.client.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.br.aws.client.entity.ProductEntity;

public interface ProductRepository extends CrudRepository<ProductEntity, Long> {

	Optional<ProductEntity> findByCode(String code);
}
