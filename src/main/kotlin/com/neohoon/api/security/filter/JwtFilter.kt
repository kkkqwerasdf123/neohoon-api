package com.neohoon.api.security.filter

import com.neohoon.api.security.authentication.TokenProvider
import com.neohoon.api.security.authentication.TokenValidateState.*
import com.neohoon.api.security.service.AuthService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val tokenProvider: TokenProvider,
    private val authService: AuthService,
): OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val ignoreUriPatterns = listOf("/api/v1/authenticate").map { AntPathRequestMatcher.antMatcher(it) }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        if (isIgnoreRequest(request)) {

            log.debug("request ignored JWT filter")

            filterChain.doFilter(request, response)
            return
        }

        val accessToken = obtainAccessToken(request)

        if (StringUtils.hasText(accessToken)) {
            when (tokenProvider.accessTokenValidState(accessToken)) {

                VALID -> authService.setAuthentication(accessToken)

                EXPIRED -> {
                    val refreshToken = obtainRefreshToken(request)

                    log.debug("refreshToken in cookie : {}", refreshToken)

                    authService.refreshToken(refreshToken, accessToken)
                        ?.let {
                            response.setHeader(AuthService.AUTHORIZATION_HEADER_NAME, it.accessToken)
                            response.setHeader("Set-Cookie", authService.getRefreshTokenCookie(it.refreshToken).toString())

                            log.debug("token refreshed")
                        }
                }

                INVALID -> {}
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun obtainAccessToken(request: HttpServletRequest): String {

        val bearerToken = request.getHeader(AuthService.AUTHORIZATION_HEADER_NAME)

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return ""
    }

    private fun obtainRefreshToken(request: HttpServletRequest): String {
        return (request.cookies ?: arrayOf())
            .filter { it.name == AuthService.REFRESH_TOKEN_COOKIE_NAME }
            .map { it.value }
            .firstOrNull()
            ?: ""
    }

    private fun isIgnoreRequest(request: HttpServletRequest): Boolean {
        return ignoreUriPatterns.any { it.matches(request) }
    }

}