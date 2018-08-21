package com.jweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages={"com.jweb"})
public class JwebApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwebApplication.class, args);
	}
}
