package com.luvoong.api.security.web

import com.luvoong.api.security.dto.LoginRequest
import com.luvoong.api.security.service.AuthService
import jakarta.servlet.http.HttpServletResponse
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
    fun authenticate(loginRequest: LoginRequest, response: HttpServletResponse): ResponseEntity<Void> {

        val tokenDto = authService.authenticate(loginRequest.username, loginRequest.password)

        val headers = HttpHeaders()
        headers.set(AuthService.AUTHORIZATION_HEADER_NAME, tokenDto.accessToken)
        response.setHeader("Set-Cookie", authService.getRefreshTokenCookie(tokenDto.refreshToken).toString())

        return ResponseEntity(headers, HttpStatus.OK)
    }

}