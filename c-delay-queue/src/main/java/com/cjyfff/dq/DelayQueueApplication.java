package com.cjyfff.dq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"com.cjyfff.dq"})
@ComponentScan(basePackages={"com.cjyfff.election"})
public class DelayQueueApplication {

	public static void main(String[] args) {
		SpringApplication.run(DelayQueueApplication.class, args);
	}
}
