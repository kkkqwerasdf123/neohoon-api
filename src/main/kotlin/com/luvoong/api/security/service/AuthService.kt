package com.luvoong.api.security.service

import com.luvoong.api.app.domain.member.MemberToken
import com.luvoong.api.app.exception.security.MemberNotFoundException
import com.luvoong.api.app.repository.member.MemberRepository
import com.luvoong.api.security.authentication.CustomAuthenticationProvider
import com.luvoong.api.security.authentication.TokenProvider
import com.luvoong.api.security.dto.TokenDto
import com.luvoong.api.security.repository.MemberTokenRepository
import com.luvoong.api.security.userdetails.CustomUserDetailsService
import com.luvoong.api.security.userdetails.UserInfo
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseCookie
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
    private val tokenProvider: TokenProvider,
    private val userDetailsService: CustomUserDetailsService
) {

    companion object {

        const val AUTHORIZATION_HEADER_NAME = "Authorization"
        const val REFRESH_TOKEN_COOKIE_NAME = "lvrt"
        const val REFRESH_TOKEN_TIME_TO_LIVE: Long = 14 * 86400

    }

    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun authenticate(username: String, password: String): TokenDto {

        val authentication = authenticationProvider.authenticate(
            UsernamePasswordAuthenticationToken(
                memberRepository.findByEmailAndDeletedIsFalse(username)
                    .map { it.email }
                    .orElseThrow { MemberNotFoundException() },
                password
            )
        )

        setAuthentication(authentication)

        val user = authentication.principal as UserInfo

        return TokenDto(tokenProvider.createToken(user), generateRefreshToken(username, user.key))
    }

    @Transactional
    fun authenticateForOAuth(username: String): TokenDto {
        val user = userDetailsService.loadUserByUsername(username) as UserInfo

        val authentication = UsernamePasswordAuthenticationToken(user, null, user.authorities)

        setAuthentication(authentication)

        return TokenDto(tokenProvider.createToken(user), generateRefreshToken(user.username, user.key))
    }

    @Transactional
    fun generateRefreshToken(username: String, key: String): String {
        val memberToken = MemberToken(username, key)
        memberTokenRepository.save(memberToken)
        log.debug("refreshToken generated")
        return memberToken.token
    }

    @Transactional
    fun refreshToken(token: String, jwt: String): Optional<TokenDto> {
        val user = tokenProvider.getUserOfExpiredToken(jwt)
        return memberTokenRepository.findById(token)
            .filter {user.username == it.username && user.key == it.key }
            .map {
                try {
                    val authentication = authenticationProvider.getAuthenticationByUsername(user.username)
                    memberTokenRepository.delete(it)
                    setAuthentication(authentication)
                    val freshUser = authentication.principal as UserInfo
                    TokenDto(tokenProvider.createToken(freshUser), generateRefreshToken(freshUser.username, freshUser.key))
                } catch (e: MemberNotFoundException) {
                    log.debug("refresh token is valid but member not exists")
                    null
                }
            }
    }


    fun setAuthentication(authentication: Authentication) {
        SecurityContextHolder.getContext().authentication = authentication
    }

    fun setAuthentication(jwt: String) {
        setAuthentication(tokenProvider.getAuthentication(jwt))
    }

    fun getRefreshTokenCookie(refreshToken: String): ResponseCookie {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME)
            .value(refreshToken)
            .sameSite("None")
            .httpOnly(true)
            .path("/")
            .maxAge(REFRESH_TOKEN_TIME_TO_LIVE)
            .secure(true)
            .build()
    }
}