package com.neohoon.api.app.controller.test

import com.neohoon.api.app.domain.member.Member
import com.neohoon.api.app.domain.member.MemberRole
import com.neohoon.api.app.domain.member.Role
import com.neohoon.api.app.exception.NeohoonBaseException
import com.neohoon.api.app.repository.member.MemberRepository
import com.neohoon.api.app.repository.member.MemberRoleRepository
import com.neohoon.api.app.service.TestService
import com.neohoon.api.security.userdetails.UserInfo
import jakarta.servlet.http.HttpServletRequest
import org.jasypt.encryption.StringEncryptor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Profile("!prod && !dev")
@RestController
class TestController (
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val testService: TestService,

    @Qualifier(value = "jasyptStringEncryptor")
    val encryptor: StringEncryptor
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    fun home() = "Hello"

    @Transactional
    @PostMapping("/dev/v1/test/member")
    fun resetTestMember(): ResponseEntity<Void> {
        val member = Member("kkkqwerasdf123@naver.com", "kkkqwerasdf123@naver.com", "DongHyeok Kim", LocalDate.of(1991, 12, 17))
        member.password = passwordEncoder.encode("1234")
        member.roles.add(MemberRole(member, Role.USER))
        member.roles.add(MemberRole(member, Role.MASTER))
        memberRepository.save(
            member
        )
        memberRoleRepository.saveAll(member.roles)
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
            throw NeohoonBaseException(HttpStatus.BAD_GATEWAY, "test.name")
        }
    }

    @GetMapping("/dev/v1/test/encrypt")
    fun encrypt(@RequestParam("text") text: String): ResponseEntity<String> {
        return ResponseEntity.ok(encryptor.encrypt(text))
    }

}