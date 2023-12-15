package br.com.aws.client.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.aws.client.entity.ProductEntity;
import br.com.aws.client.enuns.EventTypeEnum;
import br.com.aws.client.repository.ProductRepository;
import br.com.aws.client.service.ProductPublisherService;

@RestController
@RequestMapping("/api/products")
public class ProductAPI {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductPublisherService productPublisherService;

	@GetMapping
	public Iterable<ProductEntity> findAll() {
		return productRepository.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductEntity> findById(@PathVariable long id) {
		Optional<ProductEntity> optProductEntity = productRepository.findById(id);
		if (optProductEntity.isPresent()) {
			return new ResponseEntity<ProductEntity>(optProductEntity.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping
	public ResponseEntity<ProductEntity> saveProductEntity(@RequestBody ProductEntity product) {
		ProductEntity productCreated = productRepository.save(product);
		
		this.productPublisherService.publishProductEvent(productCreated, EventTypeEnum.PRODUCT_CREATED, "user-save");

		return new ResponseEntity<ProductEntity>(productCreated, HttpStatus.CREATED);
	}

	@PutMapping(path = "/{id}")
	public ResponseEntity<ProductEntity> updateProductEntity(@RequestBody ProductEntity product,
			@PathVariable("id") long id) {
		if (productRepository.existsById(id)) {
			product.setId(id);

			ProductEntity productUpdated = productRepository.save(product);
			
			this.productPublisherService.publishProductEvent(productUpdated, EventTypeEnum.PRODUCT_CREATED, "user-update");

			return new ResponseEntity<ProductEntity>(productUpdated, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<ProductEntity> deleteProductEntity(@PathVariable("id") long id) {
		Optional<ProductEntity> optProductEntity = productRepository.findById(id);
		if (optProductEntity.isPresent()) {
			ProductEntity product = optProductEntity.get();

			productRepository.delete(product);
			
			this.productPublisherService.publishProductEvent(product, EventTypeEnum.PRODUCT_DELETED, "user-delete");

			return new ResponseEntity<ProductEntity>(product, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(path = "/bycode")
	public ResponseEntity<ProductEntity> findByCode(@RequestParam String code) {
		Optional<ProductEntity> optProductEntity = productRepository.findByCode(code);
		if (optProductEntity.isPresent()) {
			return new ResponseEntity<ProductEntity>(optProductEntity.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
