package com.neohoon.api.app.domain.member

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*

/**
 * 회원 refreshToken
 * 삭제/저장 정책 검토 (redis)
 */
@Entity
@Table(name = "member_token")
class MemberToken(

    username: String,
    key: String

) {

    @Id
    var token: String = UUID.randomUUID().toString()

    val username: String = username

    val validationKey: String = key

    val expireDate: LocalDateTime = LocalDateTime.now().plusDays(14)

}