package ru.hse.security_service.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import ru.hse.security_service.client.rest.api.DatabaseProviderApi
import ru.hse.security_service.model.User
import ru.hse.security_service.model.converter.UserSecurityDtoToUserDetailsConverter

@Service
class NewUserDetailsService(
    private val databaseProviderApi: DatabaseProviderApi,
    private val userSecurityDtoToUserDetailsConverter: UserSecurityDtoToUserDetailsConverter
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails? {
        return databaseProviderApi.getUserByEmail(email)?.let{ userSecurityDto ->
            userSecurityDtoToUserDetailsConverter.convert(userSecurityDto)
        }
    }

    fun loadUserByEmailAndPassword(email: String, password: String): User? {
        return databaseProviderApi.login(email, password)?.let { userSecurityDto ->
            userSecurityDtoToUserDetailsConverter.convert(userSecurityDto)
        }
    }
}
