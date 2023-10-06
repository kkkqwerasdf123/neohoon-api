package com.luvoong.api.app.dto

import com.luvoong.api.app.domain.member.Member
import com.luvoong.api.app.domain.member.Role
import java.time.LocalDate

class MemberDto(
    member: Member
) {
    val name: String? = member.name
    val email: String? = member.email
    val birthday: LocalDate? = member.birthday
    val authorities: List<Role> = member.roles.map { it.role }

}
