package com.nami.springbootinit.proxytest2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author nami404
 * * @date 2025/2/27 17:04
 */
@Configuration
public class ProjectConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
