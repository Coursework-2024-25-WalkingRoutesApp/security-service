package ru.hse.security_service.model.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import ru.hse.security_service.dto.UserSecurityDto
import ru.hse.security_service.model.User

@Component
class UserSecurityDtoToUserDetailsConverter : Converter<UserSecurityDto, UserDetails> {

    override fun convert(source: UserSecurityDto): User? =
        User(
            id = source.id,
            userName = source.username,
            email = source.email,
            password = source.password,
            role = source.roles.map { User.AuthorityType.valueOf(it) }
                .firstOrNull() ?: User.AuthorityType.DEFAULT,
            isVerified = source.isVerified
        )
}
