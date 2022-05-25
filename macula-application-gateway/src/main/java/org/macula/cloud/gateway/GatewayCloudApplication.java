package org.macula.cloud.gateway;

import reactor.core.publisher.Hooks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayCloudApplication {

	public static void main(String[] args) {
		Hooks.onOperatorDebug();
		SpringApplication.run(GatewayCloudApplication.class, args);
	}
}
