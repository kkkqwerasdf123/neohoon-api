package com.luvoong.api.security.repository

import com.luvoong.api.app.domain.member.MemberToken
import org.springframework.data.repository.CrudRepository

interface MemberTokenRepository: CrudRepository<MemberToken, String> {
}