package com.luvoong.api.config

import com.luvoong.api.security.filter.JwtFilter
import com.luvoong.api.security.handler.CustomAccessDeniedHandler
import com.luvoong.api.security.handler.CustomAuthenticationEntryPoint
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.*
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val authenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val accessDeniedHandler: CustomAccessDeniedHandler,
    private val jwtFilter: JwtFilter,

    @Value("\${luvoong.web.cors.allowed-origins}")
    private val allowedOrigins: Array<String>
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        with(http) {
            csrf { it.disable() }
            headers { it.disable() }
            cors { it.configurationSource(corsConfigurationSource()) }
            authorizeHttpRequests {
                it.requestMatchers(*antMatchers("/api/v1/authenticate")).permitAll()
                it.requestMatchers(*antMatchers("/dev/v1/**")).permitAll()
                it.requestMatchers(*antMatchers("/api/v1/**")).authenticated()
                it.anyRequest().authenticated()
            }
            sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            exceptionHandling {
                it.accessDeniedHandler(accessDeniedHandler)
                it.authenticationEntryPoint(authenticationEntryPoint)
            }
            addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()

        config.allowCredentials = true
        config.allowedOrigins = allowedOrigins.toMutableList()
        config.allowedMethods = mutableListOf(GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD).map { it.name() }
        config.allowedHeaders = mutableListOf("*")
        config.exposedHeaders = mutableListOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    private fun antMatchers(vararg patterns: String): Array<RequestMatcher> {
        return patterns.map { AntPathRequestMatcher(it) }.toTypedArray()
    }

}