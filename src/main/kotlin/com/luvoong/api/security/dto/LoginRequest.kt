package com.luvoong.api.security.dto

data class LoginRequest(
    val username: String,
    val password: String,
)
