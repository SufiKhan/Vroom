package com.example.trading.vroom.config;

import com.example.trading.vroom.domain.UserAccount;
import com.example.trading.vroom.service.MatchingEngineService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class BootstrapDataConfig {

    @Bean
    public CommandLineRunner seedVroomData(MatchingEngineService matchingEngineService) {
        return args -> {
            UserAccount user1 = matchingEngineService.getOrCreateAccount("user_1");
            user1.addPosition("VROOM", BigDecimal.valueOf(5));
            matchingEngineService.saveAccount(user1);

            UserAccount user2 = matchingEngineService.getOrCreateAccount("user_2");
            user2.addPosition("VROOM", BigDecimal.valueOf(5));
            matchingEngineService.saveAccount(user2);
        };
    }
}
