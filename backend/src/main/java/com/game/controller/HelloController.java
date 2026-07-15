package com.game.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Temporary smoke-test endpoint. Remove in Phase 2 when game APIs land.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class HelloController {

	@GetMapping("/hello")
	public Map<String, String> hello() {
		return Map.of("message", "Hello from AVZ");
	}
}
