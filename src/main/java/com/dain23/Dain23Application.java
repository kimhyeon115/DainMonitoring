package com.dain23;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.dain23")
@EnableScheduling
public class Dain23Application {

	public static void main(String[] args) {
		SpringApplication.run(Dain23Application.class, args);
	}

}
