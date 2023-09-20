package com.luvoong.api.security.service

import com.luvoong.api.app.domain.member.MemberToken
import com.luvoong.api.app.exception.security.MemberNotFoundExceptionLuvoong
import com.luvoong.api.app.repository.member.MemberRepository
import com.luvoong.api.security.authentication.CustomAuthenticationProvider
import com.luvoong.api.security.repository.MemberTokenRepository
import com.luvoong.api.security.authentication.TokenProvider
import com.luvoong.api.security.dto.TokenDto
import com.luvoong.api.security.userdetails.UserInfo
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AuthService(
    private val authenticationProvider: CustomAuthenticationProvider,
    private val memberRepository: MemberRepository,
    private val memberTokenRepository: MemberTokenRepository,
    private val tokenProvider: TokenProvider
) {

    @Transactional
    fun authenticateAndGetToken(username: String, password: String): TokenDto {

        val authentication = authenticationProvider.authenticate(
            UsernamePasswordAuthenticationToken(
                memberRepository.findByEmailAndDeletedIsFalse(username)
                    .map { it.email }
                    .orElseThrow { MemberNotFoundExceptionLuvoong() },
                password
            )
        )

        setAuthenticationToSecurityContext(authentication)

        return TokenDto(tokenProvider.createToken(authentication.principal as UserInfo), generateRefreshToken(username))
    }

    @Transactional
    fun generateRefreshToken(username: String): String {
        val memberToken = MemberToken(username)
        memberTokenRepository.save(memberToken)
        return memberToken.token
    }

    @Transactional
    fun refreshToken(token: String, jwt: String): Optional<TokenDto> {
        val user = tokenProvider.getUserOfExpiredToken(jwt)
        return memberTokenRepository.findById(token)
            .filter { user.username == it.username }
            .map {
                val authentication = authenticationProvider.getAuthenticationByUsername(user.username)
                memberTokenRepository.delete(it)
                setAuthenticationToSecurityContext(authentication)
                Optional.of(TokenDto(tokenProvider.createToken(authentication.principal as UserInfo), generateRefreshToken(user.username)))
            }
            .orElse(Optional.empty())
    }


    fun setAuthenticationToSecurityContext(authentication: Authentication) {
        SecurityContextHolder.getContext().authentication = authentication
    }

    fun setAuthenticationToSecurityContext(jwt: String) {
        setAuthenticationToSecurityContext(tokenProvider.getAuthentication(jwt))
    }

}