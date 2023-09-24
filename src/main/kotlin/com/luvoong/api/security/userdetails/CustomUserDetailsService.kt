package com.luvoong.api.security.userdetails

import com.luvoong.api.app.domain.member.Member
import com.luvoong.api.app.exception.security.MemberNotFoundException
import com.luvoong.api.app.repository.member.MemberRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class CustomUserDetailsService(
    private val memberRepository: MemberRepository
): UserDetailsService {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun loadUserByUsername(username: String): UserDetails {

        log.debug("loadUserByUsername / username: {}", username)

        return memberRepository.findByEmailAndDeletedIsFalse(username)
            .map { createUserInfo(it) }
            .orElseThrow { MemberNotFoundException() }
    }

    private fun createUserInfo(member: Member): UserInfo {

        val authorities = member.roles
            .map { SimpleGrantedAuthority("ROLE_${it.role.name}") }
            .toMutableList()

        return UserInfo(
            id = member.id!!,
            email = member.email,
            password = member.password,
            authorities = authorities
        )
    }

}