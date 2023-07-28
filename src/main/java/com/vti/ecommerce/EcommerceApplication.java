package com.vti.ecommerce;

import com.vti.ecommerce.service.FileService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcommerceApplication implements CommandLineRunner {

	@Resource
	private FileService fileService;

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}

	@Override
	public void run(String... arg) throws Exception {
		fileService.init();
	}
}
