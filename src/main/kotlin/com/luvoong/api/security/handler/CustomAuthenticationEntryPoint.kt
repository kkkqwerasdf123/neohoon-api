package com.luvoong.api.security.handler

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
): AuthenticationEntryPoint {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        log.debug("unauthorized : {}", request.requestURI)
        with (response) {
            contentType = MediaType.APPLICATION_JSON.toString()
            characterEncoding = StandardCharsets.UTF_8.name()
            status = HttpStatus.UNAUTHORIZED.value()
            objectMapper.writeValue(writer, ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED))
        }
    }

}