package com.cjyfff;

import com.cjyfff.api.dq.conf.ClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@RibbonClients({@RibbonClient(name = "delay-queue-service", configuration = ClientConfig.class)})
public class CApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CApiApplication.class, args);
	}

}
