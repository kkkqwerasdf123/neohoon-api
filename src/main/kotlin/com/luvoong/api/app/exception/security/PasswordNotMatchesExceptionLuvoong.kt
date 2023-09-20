package com.luvoong.api.app.exception.security

import com.luvoong.api.app.exception.LuvoongBaseException
import org.springframework.http.HttpStatus

class PasswordNotMatchesExceptionLuvoong : LuvoongBaseException(
    HttpStatus.UNAUTHORIZED
)