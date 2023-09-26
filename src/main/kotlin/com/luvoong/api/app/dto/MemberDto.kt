package com.luvoong.api.app.dto

import java.time.LocalDate

data class MemberDto(
    val name: String?,
    val email: String?,
    val birthday: LocalDate?
)