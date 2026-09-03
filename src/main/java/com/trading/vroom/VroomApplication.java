package com.trading.vroom;

import com.trading.vroom.config.MarketSimulationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MarketSimulationProperties.class)
public class VroomApplication {

	public static void main(String[] args) {
		SpringApplication.run(VroomApplication.class, args);
	}

}
