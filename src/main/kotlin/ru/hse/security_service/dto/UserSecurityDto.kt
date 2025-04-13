package ru.hse.security_service.dto

import java.util.UUID

class UserSecurityDto(
    var id: UUID,
    var username: String,
    var email: String,
    var password: String,
    var roles: List<String>
)