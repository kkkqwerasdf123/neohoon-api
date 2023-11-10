package com.neohoon.api.app.service

import com.neohoon.api.app.dto.MemberDto
import com.neohoon.api.app.repository.member.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository
) {

    @Transactional(readOnly = true)
    fun findMemberDtoById(id: Long): MemberDto {
        return memberRepository.findMemberDtoById(id)
            ?: throw RuntimeException()
    }

}