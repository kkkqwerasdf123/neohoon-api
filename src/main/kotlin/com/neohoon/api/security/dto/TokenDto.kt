package com.neohoon.api.security.dto

data class TokenDto(
    val accessToken: String,
    val refreshToken: String
)