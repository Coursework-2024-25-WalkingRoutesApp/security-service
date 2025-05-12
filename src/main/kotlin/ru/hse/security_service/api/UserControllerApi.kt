package ru.hse.security_service.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestParam
import ru.hse.security_service.model.User

@Tag(name = "Контроллер пользователей", description = "Контроллер для работы с пользователями: регистрация, вход, обновление информации и проверка статуса")
@ApiResponses(
    value = [
        ApiResponse(responseCode = "200", description = "Успешный запрос"),
        ApiResponse(responseCode = "201", description = "Создано"),
        ApiResponse(responseCode = "400", description = "Некорректный запрос",
            content = [Content(mediaType = "text/plain", schema = Schema(implementation = String::class))]),
        ApiResponse(responseCode = "401", description = "Не авторизован",
            content = [Content(mediaType = "text/plain", schema = Schema(implementation = String::class))]),
        ApiResponse(responseCode = "404", description = "Не найдено",
            content = [Content(mediaType = "text/plain", schema = Schema(implementation = String::class))]),
        ApiResponse(responseCode = "500", description = "Ошибка сервера",
            content = [Content(mediaType = "text/plain", schema = Schema(implementation = String::class))])
    ]
)
interface UserControllerApi {

    @Operation(
        summary = "Регистрация нового пользователя",
        description = "Создаёт нового пользователя с указанными данными"
    )
    fun register(
        @RequestParam username: String,
        @RequestParam email: String,
        @RequestParam password: String
    ): ResponseEntity<String>

    @Operation(
        summary = "Вход пользователя в систему",
        description = "Авторизует пользователя по email и паролю"
    )
    fun login(
        @RequestParam email: String,
        @RequestParam password: String
    ): ResponseEntity<String>

    @Operation(
        summary = "Получение информации о пользователе",
        description = "Возвращает информацию о пользователе по ID"
    )
    fun getUserInfo(
        @AuthenticationPrincipal user: User?
    ): ResponseEntity<Any>

    @Operation(
        summary = "Обновление имени пользователя",
        description = "Обновляет имя пользователя по ID"
    )
    fun updateUsername(
        @AuthenticationPrincipal user: User?,
        @RequestParam newUsername: String
    ): ResponseEntity<Any>

    @Operation(
        summary = "Обновление фотографии пользователя",
        description = "Обновляет фотографию пользователя по URL"
    )
    fun updateUserPhoto(
        @AuthenticationPrincipal user: User?,
        @RequestParam photoUrl: String
    ): ResponseEntity<Any>

    @Operation(
        summary = "Отправка кода подтверждения на email",
        description = "Отправляет код подтверждения на указанный email"
    )
    fun sendVerificationCode(
        @AuthenticationPrincipal user: User?,
        @RequestParam email: String
    ): ResponseEntity<Any>

    @Operation(
        summary = "Проверка статуса подтверждения пользователя",
        description = "Проверяет, прошёл ли пользователь верификацию"
    )
    fun checkVerified(
        @AuthenticationPrincipal user: User?
    ): ResponseEntity<Any>

    @Operation(
        summary = "Проверка кода подтверждения",
        description = "Проверяет код подтверждения, введённый пользователем"
    )
    fun checkVerificationCode(
        @AuthenticationPrincipal user: User?,
        @RequestParam verificationCode: String
    ): ResponseEntity<Any>
}
