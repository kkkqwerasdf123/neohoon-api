package com.luvoong.api.security.filter

import com.luvoong.api.security.authentication.TokenProvider
import com.luvoong.api.security.authentication.TokenValidateState.*
import com.luvoong.api.security.service.AuthService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val tokenProvider: TokenProvider,
    private val authService: AuthService,
): OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val accessToken = getAccessTokenOfRequestHeader(request)

        if (StringUtils.hasText(accessToken)) {
            when (tokenProvider.accessTokenValidState(accessToken)) {

                VALID -> authService.setAuthenticationToSecurityContext(accessToken)

                EXPIRED -> {
                    val refreshToken = getRefreshTokenOfRequestCookie(request)

                    log.debug("refreshToken exists in cookies: {}", refreshToken)

                    authService.refreshToken(refreshToken, accessToken)
                        .ifPresent {
                            response.setHeader(AuthService.AUTHORIZATION_HEADER_NAME, it.accessToken)
                            response.addCookie(authService.getRefreshTokenCookie(it.refreshToken))

                            log.debug("token refreshed")
                        }
                }

                INVALID -> {}
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun getAccessTokenOfRequestHeader(request: HttpServletRequest): String {

        val bearerToken = request.getHeader(AuthService.AUTHORIZATION_HEADER_NAME)

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return ""
    }

    private fun getRefreshTokenOfRequestCookie(request: HttpServletRequest): String {
        return (request.cookies ?: arrayOf())
            .filter { it.name == AuthService.REFRESH_TOKEN_COOKIE_NAME }
            .map { it.value }
            .firstOrNull()
            ?: ""
    }

}