package com.luvoong.api.config.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class TraceIdAop(
    private val traceIdProvider: TraceIdProvider
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Around(value = "execution(* com.luvoong.api.app..*.*(..)) && (@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Repository) || @within(org.springframework.stereotype.Service))")
    fun trace(joinPoint: ProceedingJoinPoint): Any? {
        val traceId = traceIdProvider.getId()
        log.debug("> {}:{}", traceId?.id, joinPoint.signature.toShortString())
        try {
            return joinPoint.proceed(joinPoint.args)
        } finally {
            log.debug("< {}:{}", traceId?.id, joinPoint.signature.toShortString())
        }
    }

}