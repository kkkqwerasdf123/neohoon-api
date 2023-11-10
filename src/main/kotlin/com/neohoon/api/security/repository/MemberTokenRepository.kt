package com.neohoon.api.security.repository

import com.neohoon.api.app.domain.member.MemberToken
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberTokenRepository: CrudRepository<MemberToken, String> {
}