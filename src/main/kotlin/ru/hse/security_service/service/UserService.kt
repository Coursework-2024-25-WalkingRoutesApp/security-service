package ru.hse.security_service.service

import feign.FeignException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.hse.security_service.client.rest.api.DatabaseProviderApi
import ru.hse.security_service.client.rest.api.NotificationServiceApi
import ru.hse.security_service.dto.EmailRequest
import ru.hse.security_service.dto.UserDto
import java.util.*
import kotlin.io.encoding.ExperimentalEncodingApi

@Service
@ExperimentalEncodingApi
class UserService(
    private val defaultUserDetailsService: NewUserDetailsService,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
    private val databaseProviderApi: DatabaseProviderApi,
    private val notificationServiceApi: NotificationServiceApi
) {

    fun registerNewUser(name: String, email: String, password: String): ResponseEntity<String> {
        return try {
            val encodedPassword = passwordEncoder.encode(password)

            val response = databaseProviderApi.register(name, email, encodedPassword)

            if (response.statusCode.is2xxSuccessful) {
                val userDetails = defaultUserDetailsService.loadUserByEmailAndPassword(email, encodedPassword)
                val jwt = jwtService.generateToken(userDetails ?: throw Exception("User not found"))

                logger.info("User with email $email successfully registered")

                sendVerificationCode(userDetails.id!!, email)

                ResponseEntity.status(HttpStatus.CREATED).body(jwt)
            } else {
                response
            }
        } catch (e: FeignException) {
            logger.error("Feign error while registering user: ${e.status()}, ${e.contentUTF8()}", e)
            ResponseEntity.status(e.status()).body(e.contentUTF8())
        } catch (e: Exception) {
            logger.error("Error while registering user", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при регистрации пользователя")
        }
    }

    fun login(email: String, password: String): ResponseEntity<String> {
        return try {
            val userSecurityDto = databaseProviderApi.login(email, password)
                ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь с таким email не найден")

            if (!passwordEncoder.matches(password, userSecurityDto.password)) {
                logger.error("Invalid password for user with email $email")
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неверный пароль")
            }

            val userDetails = defaultUserDetailsService.loadUserByEmailAndPassword(email, userSecurityDto.password)
            val jwt = jwtService.generateToken(userDetails ?: throw Exception("User not found"))

            logger.info("User with email $email successfully logged in")
            ResponseEntity.status(HttpStatus.OK).body(jwt)
        } catch (e: FeignException) {
            logger.error("Feign error while login user: ${e.status()}, ${e.contentUTF8()}", e)
            ResponseEntity.status(e.status()).body(e.contentUTF8())
        } catch (e: Exception) {
            logger.error("Error while login user", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка входа пользователя в систему")
        }
    }

    fun getUserInfo(userId: UUID): UserDto? {
        return try {
            val response = databaseProviderApi.getUserInfo(userId)
            response?.body
        } catch (e: Exception) {
            logger.error("Error while fetching user info", e)
            null
        }
    }

    fun updateUsername(userId: UUID, newUsername: String): String {
        return try {
            databaseProviderApi.updateUsername(newUsername, userId).body.toString()
        } catch (e: Exception) {
            logger.error("Error while updating username", e)
            ""
        }
    }

    fun updateUserPhoto(userId: UUID, photoUrl: String): String {
        return try {
            databaseProviderApi.updateUserPhoto(userId, photoUrl).body.toString()
        } catch (e: Exception) {
            logger.error("Error while updating user photo", e)
            ""
        }
    }

    fun sendVerificationCode(userId: UUID, email: String): ResponseEntity<String> {
        return try {
            val verificationCode = (100_000..999_999).random().toString()

            val saveCodeResponse =
                databaseProviderApi.saveVerificationCode(userId, passwordEncoder.encode(verificationCode))
            if (!saveCodeResponse.statusCode.is2xxSuccessful) {
                logger.error("Failed to save verification code for user $userId: ${saveCodeResponse.body}")
                return ResponseEntity.status(saveCodeResponse.statusCode)
                    .body(saveCodeResponse.body ?: "Ошибка при сохранении кода подтверждения")
            }

            val sendEmailResponse = notificationServiceApi.sendEmail(
                EmailRequest(
                    subject = "Код подтверждения для Пойдем.Daily",
                    targetEmail = email,
                    text = "Твой код подтверждения: $verificationCode"
                )
            )
            if (!sendEmailResponse.statusCode.is2xxSuccessful) {
                logger.error("Failed to send email to $email: ${sendEmailResponse.body}")
                return ResponseEntity.status(sendEmailResponse.statusCode)
                    .body(sendEmailResponse.body ?: "Ошибка при отправке письма с кодом подтверждения")
            }

            logger.info("Verification code sent to $email")
            ResponseEntity.status(HttpStatus.OK).body("Код подтверждения отправлен на $email")
        } catch (e: FeignException) {
            logger.error("Feign error while sending verification code: ${e.status()}, ${e.contentUTF8()}", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.contentUTF8())
        } catch (e: Exception) {
            logger.error("Error while sending verification code", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при отправке кода подтверждения")
        }
    }

    fun checkVerified(userId: UUID): ResponseEntity<Boolean> {
        return try {
            databaseProviderApi.checkVerified(userId)
        } catch (e: Exception) {
            logger.error("Error while checking if user is verified", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false)
        }
    }

    fun checkVerificationCode(userId: UUID, verificationCode: String): ResponseEntity<String> {
        return try {
            val getCodeResponse = databaseProviderApi.getVerificationCode(userId)

            val encodedCode = getCodeResponse.body
            if (encodedCode != null && passwordEncoder.matches(verificationCode, encodedCode)) {
                val updateStatusResponse = databaseProviderApi.updateVerificationStatus(userId)
                if (!updateStatusResponse.statusCode.is2xxSuccessful) {
                    logger.error("Failed to update verification status for user $userId: ${updateStatusResponse.body}")
                    return ResponseEntity.status(updateStatusResponse.statusCode).body(updateStatusResponse.body ?: "Ошибка при обновлении статуса подтверждения")
                }

                ResponseEntity.status(HttpStatus.OK).body("Код подтверждения верный")
            } else {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Код подтверждения неверный")
            }
        } catch (e: FeignException) {
            logger.error("Error while checking verification code: ${e.status()}, ${e.contentUTF8()}", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.contentUTF8())
        } catch (e: Exception) {
            logger.error("Error while checking verification code", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при проверке кода подтверждения")
        }
    }


    companion object {
        private val logger = LoggerFactory.getLogger(UserService::class.java)
    }
}
