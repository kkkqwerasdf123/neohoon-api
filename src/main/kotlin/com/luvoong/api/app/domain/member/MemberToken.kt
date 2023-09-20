package com.luvoong.api.app.domain.member

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.util.*

@RedisHash(value = "member_token", timeToLive = 14 * 86400)
class MemberToken(

    username: String,
    key: String

) {

    @Id
    var token: String = UUID.randomUUID().toString()

    var username: String = username

    var key: String = key

}