package com.dain23.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {

	@GetMapping("/")
	public String serverStatus() {
		return "Server is running and stable. Time: " + java.time.LocalDateTime.now();
	}
}
