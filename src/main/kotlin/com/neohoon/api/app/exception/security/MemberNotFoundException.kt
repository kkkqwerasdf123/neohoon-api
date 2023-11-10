package com.neohoon.api.app.exception.security

import com.neohoon.api.app.exception.NeohoonBaseException
import org.springframework.http.HttpStatus

class MemberNotFoundException: NeohoonBaseException(
    HttpStatus.UNAUTHORIZED
)