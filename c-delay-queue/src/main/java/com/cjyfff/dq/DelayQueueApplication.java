package com.cjyfff.dq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(basePackages={"com.cjyfff.dq"})
@ComponentScan(basePackages={"com.cjyfff.election"})
public class DelayQueueApplication {

	public static void main(String[] args) {
		SpringApplication.run(DelayQueueApplication.class, args);
	}
}
