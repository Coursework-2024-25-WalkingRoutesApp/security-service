package ru.hse.security_service.service.notifier

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import ru.hse.security_service.dto.EmailRequest

@Service
interface NotifierService {

    fun send(request: EmailRequest): ResponseEntity<String>
}