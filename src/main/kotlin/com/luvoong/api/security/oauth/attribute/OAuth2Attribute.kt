package com.luvoong.api.security.oauth.attribute

import com.luvoong.api.security.oauth.Provider

sealed class OAuth2Attribute(val attributes: MutableMap<String, Any>) {
    abstract val providerId: String
    abstract val provider: Provider
    abstract val email: String
}

class KakaoAttribute(attributes: MutableMap<String, Any>): OAuth2Attribute(attributes) {
    override val providerId = attributes["id"].toString()
    override val provider = Provider.kakao
    override val email = (attributes["kakao_account"] as MutableMap<*, *>)["email"].toString()
}

