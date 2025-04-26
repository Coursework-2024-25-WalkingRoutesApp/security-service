package ru.hse.security_service.dto

data class EmailRequest(
    val subject: String,
    val targetEmail: String,
    val text: String,
    val name: String? = null
)
