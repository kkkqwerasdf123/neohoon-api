package com.luvoong.api.config

import com.luvoong.api.config.interceptor.TraceInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val traceInterceptor: TraceInterceptor
): WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(traceInterceptor)
            .addPathPatterns("/**")
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/docs/**").addResourceLocations("classpath:static/docs/")
    }
}