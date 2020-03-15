package ru.tinkoff.fintech.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig {

    @Bean
    fun getRestTemplate(): RestTemplate? {
        return RestTemplate()
    }

}