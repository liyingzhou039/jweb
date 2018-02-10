package com.jweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"com.jweb.busi","com.jweb.system"}) 
public class JwebApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwebApplication.class, args);
	}
}
