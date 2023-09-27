package com.luvoong.api.app.handler

import com.luvoong.api.app.exception.LuvoongBaseException
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.ProblemDetail
import org.springframework.web.ErrorResponseException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import java.util.*

@RestControllerAdvice
class ExceptionResolver(
    private val messageSource: MessageSource
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(Exception::class)
    fun resolveException(
        ex: Exception,
        locale: Locale
    ) = when (ex) {
        is LuvoongBaseException -> getProblemDetail(ex.status, ex.code, locale)
        is ErrorResponseException -> ex.body
        is NoHandlerFoundException -> getProblemDetail(NOT_FOUND)
        is HttpRequestMethodNotSupportedException -> getProblemDetail(METHOD_NOT_ALLOWED)

        is MethodArgumentNotValidException -> getProblemDetail(BAD_REQUEST)
        else -> {
            log.error("unhandled error occurred : ", ex)
            getProblemDetail(INTERNAL_SERVER_ERROR)
        }
    }

    fun getProblemDetail(status: HttpStatus, code: String? = null, locale: Locale = Locale.getDefault()) = when (code) {
        null -> ProblemDetail.forStatus(status)
        else -> ProblemDetail.forStatusAndDetail(status, getMessageByCode(code, locale) ?: status.reasonPhrase)
    }

    fun getMessageByCode(code: String, locale: Locale): String? {
        return try {
            messageSource.getMessage(code, null, locale)
        } catch (e: NoSuchMessageException) {
            null
        }
    }

}