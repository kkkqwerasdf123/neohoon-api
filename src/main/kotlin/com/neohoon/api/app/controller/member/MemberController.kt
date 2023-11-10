package com.neohoon.api.app.controller.member

import com.neohoon.api.app.dto.MemberDto
import com.neohoon.api.app.service.MemberService
import com.neohoon.api.security.userdetails.UserInfo
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberController(
    val memberService: MemberService
) {

    @GetMapping("/api/v1/member/me")
    fun myInfo(
        @AuthenticationPrincipal user: UserInfo
    ): ResponseEntity<MemberDto> {
        return ResponseEntity.ok(memberService.findMemberDtoById(user.id))
    }

}