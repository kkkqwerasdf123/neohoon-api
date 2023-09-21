package com.luvoong.api.app.controller.test

import com.luvoong.api.app.domain.member.Member
import com.luvoong.api.app.domain.member.MemberRole
import com.luvoong.api.app.domain.member.Role
import com.luvoong.api.app.exception.LuvoongBaseException
import com.luvoong.api.app.repository.member.MemberRepository
import com.luvoong.api.app.repository.member.MemberRoleRepository
import com.luvoong.api.app.service.TestService
import com.luvoong.api.security.userdetails.UserInfo
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@Profile("test || local || default")
@RestController
class TestController (
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val testService: TestService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    fun home() = "Hello"

    @Transactional
    @PostMapping("/dev/v1/test/member")
    fun resetTestMember(): ResponseEntity<Void> {
        val member = Member("DongHyeok", "Kim", "kkkqwerasdf123@naver.com", LocalDate.of(1991, 12, 17))
        member.password = passwordEncoder.encode("1234")
        memberRepository.save(
            member
        )
        memberRoleRepository.save(MemberRole(member, Role.USER))
        memberRoleRepository.save(MemberRole(member, Role.MASTER))
        return ResponseEntity(HttpStatus.ACCEPTED)
    }

    @Transactional
    @DeleteMapping("/dev/v1/test/member")
    fun deleteALlMembers(): ResponseEntity<Void> {
        testService.deleteAllMembers()
        return ResponseEntity.ok().build()
    }

    @GetMapping("/api/v1/auth-check")
    fun test2(@AuthenticationPrincipal userInfo: UserInfo, request: HttpServletRequest): String {
        log.debug("user: {}", userInfo)
        log.debug("uri: {}", request.requestURI)
        return "hello, OK"
    }

    @GetMapping("/api/v1/ex-test")
    fun exTest() {
        if (1 == 1) {
            throw LuvoongBaseException(HttpStatus.BAD_GATEWAY, "test.name")
        }
    }

}