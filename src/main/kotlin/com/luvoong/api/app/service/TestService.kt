package com.luvoong.api.app.service

import com.luvoong.api.app.repository.member.MemberOauthRepository
import com.luvoong.api.app.repository.member.MemberRepository
import com.luvoong.api.app.repository.member.MemberRoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TestService(
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val memberOauthRepository: MemberOauthRepository
) {

    @Transactional
    fun deleteAllMembers() {
        memberOauthRepository.deleteAll()
        memberRoleRepository.deleteAll()
        memberRepository.deleteAll()
    }

}