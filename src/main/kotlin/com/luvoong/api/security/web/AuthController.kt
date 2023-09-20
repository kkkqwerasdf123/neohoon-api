package com.luvoong.api.security.web

import com.luvoong.api.security.dto.LoginRequest
import com.luvoong.api.security.filter.JwtFilter
import com.luvoong.api.security.service.AuthService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/api/v1/authenticate")
    fun authenticate(loginRequest: LoginRequest, response: HttpServletResponse): ResponseEntity<Void> {

        val tokenDto = authService.authenticateAndGetToken(loginRequest.username, loginRequest.password)

        val headers = HttpHeaders()
        headers.add(JwtFilter.AUTHORIZATION_HEADER_NAME, tokenDto.accessToken)
        val cookie = Cookie(JwtFilter.REFRESH_TOKEN_COOKIE_NAME, tokenDto.refreshToken)
        cookie.isHttpOnly = true
        response.addCookie(cookie)

        return ResponseEntity(headers, HttpStatus.OK)
    }

}