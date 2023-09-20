package com.luvoong.api.security.filter

import com.luvoong.api.config.aop.TraceId
import com.luvoong.api.security.authentication.TokenProvider
import com.luvoong.api.security.authentication.TokenValidateState.EXPIRED
import com.luvoong.api.security.authentication.TokenValidateState.VALID
import com.luvoong.api.security.service.AuthService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

@Component
class JwtFilter(
    private val tokenProvider: TokenProvider,
    private val authService: AuthService,
): OncePerRequestFilter() {

    companion object {

        const val AUTHORIZATION_HEADER_NAME = "Authorization"
        const val REFRESH_TOKEN_COOKIE_NAME = "X-LUVOONG-REFRESH-TOKEN"

    }

    private val transactionHolder = ThreadLocal<TraceId>()
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val jwt = resolveToken(request)

        if (StringUtils.hasText(jwt)) {
            val state = tokenProvider.getValidState(jwt)

            if (state == VALID) {
                authService.setAuthenticationToSecurityContext(jwt)
            } else if (state == EXPIRED) {
                val refreshToken = getRefreshToken(request)

                log.debug("refreshToken exists in cookies: {}", refreshToken)

                authService.refreshToken(refreshToken, jwt)
                    .ifPresent {
                        response.setHeader(AUTHORIZATION_HEADER_NAME, it.accessToken)
                        val cookie = Cookie(REFRESH_TOKEN_COOKIE_NAME, it.refreshToken)
                        cookie.isHttpOnly = true
                        response.addCookie(cookie)

                        log.debug("token refreshed")
                    }
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String {

        val bearerToken = request.getHeader(AUTHORIZATION_HEADER_NAME)

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return ""
    }

    private fun getRefreshToken(request: HttpServletRequest): String {
        return (request.cookies ?: arrayOf())
            .filter { it.name == REFRESH_TOKEN_COOKIE_NAME }
            .map { it.value }
            .firstOrNull()
            ?: ""
    }

}