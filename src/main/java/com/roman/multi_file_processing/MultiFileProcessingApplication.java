package com.roman.multi_file_processing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MultiFileProcessingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultiFileProcessingApplication.class, args);
	}

}
