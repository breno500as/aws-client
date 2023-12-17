package com.br.aws.client.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestAPI {

	@GetMapping
	public ResponseEntity<String> ok() {
		return ResponseEntity.ok().body("ok");
	}

}
