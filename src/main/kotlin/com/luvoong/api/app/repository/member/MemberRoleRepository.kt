package com.luvoong.api.app.repository.member

import com.luvoong.api.app.domain.member.MemberRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRoleRepository: JpaRepository<MemberRole, Long> {
}