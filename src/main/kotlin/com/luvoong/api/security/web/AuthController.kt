package com.luvoong.api.security.web

import com.luvoong.api.security.dto.LoginRequest
import com.luvoong.api.security.service.AuthService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authService: AuthService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/api/v1/authenticate")
    fun authenticate(loginRequest: LoginRequest): ResponseEntity<Void> {

        val headers = HttpHeaders().apply {

            val tokenDto = authService.authenticate(loginRequest.username, loginRequest.password)

            set(AuthService.AUTHORIZATION_HEADER_NAME, tokenDto.accessToken)
            set("Set-Cookie", authService.getRefreshTokenCookie(tokenDto.refreshToken).toString())
        }

        return ResponseEntity(headers, HttpStatus.OK)
    }

}