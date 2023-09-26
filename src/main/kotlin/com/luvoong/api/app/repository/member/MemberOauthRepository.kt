package com.luvoong.api.app.repository.member

import com.luvoong.api.app.domain.member.MemberOauth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberOauthRepository: JpaRepository<MemberOauth, Long> {
}