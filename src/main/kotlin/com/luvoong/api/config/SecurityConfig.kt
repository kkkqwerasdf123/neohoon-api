package com.luvoong.api.config

import com.luvoong.api.security.filter.JwtFilter
import com.luvoong.api.security.handler.CustomAccessDeniedHandler
import com.luvoong.api.security.handler.CustomAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

@Configuration
class SecurityConfig(
    private val authenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val accessDeniedHandler: CustomAccessDeniedHandler,
    private val jwtFilter: JwtFilter,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        with(http) {
            csrf { it.disable() }
            headers { it.disable() }
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

    private fun antMatchers(vararg patterns: String): Array<RequestMatcher> {
        return patterns.map { AntPathRequestMatcher(it) }.toTypedArray()
    }

}