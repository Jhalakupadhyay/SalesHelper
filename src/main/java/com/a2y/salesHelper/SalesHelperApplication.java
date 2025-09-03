package com.a2y.salesHelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SalesHelperApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalesHelperApplication.class, args);
	}

}
