package com.luvoong.api.config.interceptor

import com.luvoong.api.config.aop.TraceId
import com.luvoong.api.config.aop.TraceIdProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.util.UUID

@Component
class TraceInterceptor(
    private val traceIdProvider: TraceIdProvider
): HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        traceIdProvider.set(TraceId(request.getHeader("x-luvoong-tid") ?: UUID.randomUUID().toString()))
        return true;
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        traceIdProvider.remove()
    }

}