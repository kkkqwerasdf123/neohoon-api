package com.luvoong.api.security.handler

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class CustomAccessDeniedHandler(
    private val objectMapper: ObjectMapper
): AccessDeniedHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        log.debug("access denied: {}", request.requestURI)
        response.contentType = MediaType.APPLICATION_JSON.toString()
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.status = HttpStatus.FORBIDDEN.value()
        objectMapper.writeValue(response.writer, ProblemDetail.forStatus(HttpStatus.FORBIDDEN))
    }

}