package com.luvoong.api.app.repository.member

import com.luvoong.api.app.domain.member.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MemberRepository: JpaRepository<Member, Long> {

    fun findByEmailAndDeletedIsFalse(username: String): Optional<Member>

}