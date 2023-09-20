package com.luvoong.api.security.userdetails

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class UserInfo(

    id: Long,
    email: String,
    password: String?,
    authorities: MutableCollection<out GrantedAuthority>,
    key: String = UUID.randomUUID().toString().substring(0..7)

): UserDetails {

    private val authorities: MutableCollection<out GrantedAuthority> = authorities

    var id: Long = id
    private var email: String = email
    private var password: String = password ?: ""

    var key: String = key

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

}