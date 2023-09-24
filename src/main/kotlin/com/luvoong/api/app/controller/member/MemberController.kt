package com.luvoong.api.app.controller.member

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberController {

    @GetMapping("/api/v1/member/me")
    fun myInfo(): ResponseEntity<Void> {
        return ResponseEntity.ok().build()
    }

}