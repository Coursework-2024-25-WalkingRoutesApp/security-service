package ru.hse.security_service.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
class JwtProperties (
    var secret: String,
    var expiration: Long
)
