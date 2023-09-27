package com.luvoong.api.security.repository

import com.luvoong.api.app.domain.member.MemberToken
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberTokenRepository: CrudRepository<MemberToken, String> {
}