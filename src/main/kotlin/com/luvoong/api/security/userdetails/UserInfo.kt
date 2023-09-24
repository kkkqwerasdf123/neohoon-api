package com.luvoong.api.security.userdetails

import com.luvoong.api.security.oauth.attribute.OAuth2Attribute
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User
import java.util.*

class UserInfo(

    id: Long,
    email: String,
    password: String?,
    authorities: MutableCollection<out GrantedAuthority>,
    key: String = UUID.randomUUID().toString().substring(0..7),
    attribute: OAuth2Attribute? = null,

): UserDetails, OAuth2User {

    private val authorities: MutableCollection<out GrantedAuthority> = authorities

    var id: Long = id
    private var email: String = email
    private var password: String = password ?: ""
    var key: String = key

    private val attribute: OAuth2Attribute? = attribute


    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities

    override fun getPassword(): String = password

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    override fun toString(): String {
        return "UserInfo(authorities=$authorities, id=$id, email='$email', key='$key')"
    }

    override fun getName(): String? {
        return attribute?.providerId
    }

    override fun getAttributes(): MutableMap<String, Any>? {
        return attribute?.attributes
    }

}