package com.neohoon.api.security.handler

import com.neohoon.api.security.service.AuthService
import com.neohoon.api.security.userdetails.UserInfo
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class Oauth2SuccessHandler(
    private val authService: AuthService,

    @Value("\${neohoon.front.target}")
    private val frontTarget: String

): SimpleUrlAuthenticationSuccessHandler() {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as UserInfo

        log.debug("oAuth2 Login success : {}", oAuth2User)

        authService.authenticateForOAuth(oAuth2User.username).also {
            with (response) {
                setHeader(AuthService.AUTHORIZATION_HEADER_NAME, it.accessToken)
                setHeader("Set-Cookie", authService.getRefreshTokenCookie(it.refreshToken).toString())
                sendRedirect(getRedirectUrl(it.accessToken))
            }
        }

    }

    private fun getRedirectUrl(accessToken: String): String {
        return UriComponentsBuilder.fromUriString("${frontTarget}/login/oauth2")
            .queryParam("accessToken", accessToken).build().toUriString()
    }

}