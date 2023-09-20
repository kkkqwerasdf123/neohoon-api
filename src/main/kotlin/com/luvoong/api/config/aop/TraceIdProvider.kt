package com.luvoong.api.config.aop

import org.springframework.stereotype.Component

@Component
class TraceIdProvider {

    private val traceIdHolder = ThreadLocal<TraceId>()

    fun set(traceId: TraceId) {
        traceIdHolder.set(traceId)
    }

    fun getId(): TraceId? {
        return traceIdHolder.get()
    }

    fun remove() {
        traceIdHolder.remove()
    }

}