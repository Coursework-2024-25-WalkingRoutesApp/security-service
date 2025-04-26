package ru.hse.security_service.client.rest.api

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.hse.security_service.dto.UserDto
import ru.hse.security_service.dto.UserSecurityDto
import java.util.*

@FeignClient(name = "database-provider", path = "\${feign.data-provider-api.base-path}")
interface DatabaseProviderApi {

    @PostMapping("\${feign.data-provider-api.endpoints.register}")
    fun register(
        @RequestParam("username") username: String,
        @RequestParam("email") email: String,
        @RequestParam("password") password: String
    ): ResponseEntity<String>

    @GetMapping("\${feign.data-provider-api.endpoints.login}")
    fun login(
        @RequestParam("email") email: String,
        @RequestParam("password") password: String
    ): UserSecurityDto?

    @GetMapping("\${feign.data-provider-api.endpoints.get-user-info}")
    fun getUserInfo(@RequestParam("userId") userId: UUID): ResponseEntity<UserDto>?

    @PutMapping("\${feign.data-provider-api.endpoints.update-username}")
    fun updateUsername(
        @RequestParam("newUsername") newUsername: String,
        @RequestParam("userId") userId: UUID
    ): ResponseEntity<String>

    @PutMapping("\${feign.data-provider-api.endpoints.update-user-photo}")
    fun updateUserPhoto(
        @RequestParam("userId") userId: UUID,
        @RequestParam("photoUrl") photoUrl: String
    ): ResponseEntity<String>

    @GetMapping("\${feign.data-provider-api.endpoints.get-user-by-email}")
    fun getUserByEmail(@RequestParam("email") email: String): UserSecurityDto?

    @PostMapping("\${feign.data-provider-api.endpoints.save-verification-code}")
    fun saveVerificationCode(
        @RequestParam("userId") userId: UUID,
        @RequestParam("verificationCode") verificationCode: String
    ): ResponseEntity<String>

    @GetMapping("\${feign.data-provider-api.endpoints.check-verified}")
    fun checkVerified(@RequestParam("userId") userId: UUID): ResponseEntity<Boolean>

    @GetMapping("\${feign.data-provider-api.endpoints.get-verification-code}")
    fun getVerificationCode(@RequestParam("userId") userId: UUID): ResponseEntity<String>

    @PutMapping("\${feign.data-provider-api.endpoints.update-verification-status}")
    fun updateVerificationStatus(
        @RequestParam("userId") userId: UUID
    ): ResponseEntity<String>
}
