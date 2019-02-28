package com.rabbit.auth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableFeignClients
@SpringBootApplication
@ComponentScan(basePackages = {"com.rabbit.auth.server","com.rabbit.common.bean","com.rabbit.feign"})
public class RabbitAuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitAuthServerApplication.class, args);
	}

}

