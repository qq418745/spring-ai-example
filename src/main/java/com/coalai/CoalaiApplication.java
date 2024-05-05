package com.coalai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CoalaiApplication {

	/**
	 *  OpenAI API key must be set
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(CoalaiApplication.class, args);
	}

}
