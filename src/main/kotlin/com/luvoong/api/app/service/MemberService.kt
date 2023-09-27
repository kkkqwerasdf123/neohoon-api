package com.luvoong.api.app.service

import com.luvoong.api.app.dto.MemberDto
import com.luvoong.api.app.repository.member.MemberRepository
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