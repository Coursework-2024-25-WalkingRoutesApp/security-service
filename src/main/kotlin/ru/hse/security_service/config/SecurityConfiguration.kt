package ru.hse.security_service.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import ru.hse.security_service.controller.LOGIN_URL
import ru.hse.security_service.controller.REGISTER_URL
import ru.hse.security_service.controller.USER_BASE_PATH_URL
import ru.hse.security_service.filter.JwtRequestFilter
import ru.hse.security_service.service.NewUserDetailsService
import kotlin.io.encoding.ExperimentalEncodingApi

@Configuration
@ExperimentalEncodingApi
@EnableConfigurationProperties(JwtProperties::class)
class SecurityConfiguration(
    private val newUserDetailsService: NewUserDetailsService,
    private val jwtRequestFilter: JwtRequestFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder();
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(newUserDetailsService)
        provider.setPasswordEncoder(passwordEncoder())
        return provider
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { c ->
                c
                    .requestMatchers(USER_BASE_PATH_URL + REGISTER_URL, USER_BASE_PATH_URL + LOGIN_URL).permitAll()
                    .anyRequest().permitAll()//authenticated()
            }
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
            .userDetailsService(newUserDetailsService)

        return http.build()
    }
}
