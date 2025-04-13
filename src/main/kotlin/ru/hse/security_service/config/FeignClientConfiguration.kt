package ru.hse.security_service.config

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients(basePackages = ["ru.hse.security_service.client.rest.api"])
class FeignClientConfiguration
