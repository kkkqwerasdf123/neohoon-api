package com.neohoon.api.security.filter

import com.neohoon.api.app.repository.member.MemberRepository
import com.neohoon.api.app.repository.member.MemberRoleRepository
import com.neohoon.api.security.service.AuthService
import com.neohoon.api.testutil.TestUtil
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class JwtFilterTest {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var mvc: MockMvc
    @Autowired
    lateinit var restTemplate: TestRestTemplate
    @Autowired
    lateinit var memberRepository: MemberRepository
    @Autowired
    lateinit var memberRoleRepository: MemberRoleRepository

    val testUtil = TestUtil()

    @Value("\${neohoon.auth.jwt.validity-in-seconds}")
    var tokenValidityInSeconds: Long = 0

    @BeforeAll
    fun init() {

        log.info("restTemplate: {}", restTemplate)

        testUtil.restTemplate = restTemplate
        testUtil.memberRepository = memberRepository
        testUtil.memberRoleRepository = memberRoleRepository
        testUtil.insertTestMember()
    }

    @DisplayName("인증 필터 - 성공")
    @Test
    fun success() {
        mvc.perform(testUtil.get("/api/v1/auth-check"))
            .andExpect(status().isOk)
    }

    @DisplayName("인증 필터 - 성공 (만료된토큰)")
    @Test
    fun success_expired() {
        val response = testUtil.getAuthResponse()
        val accessToken = response.headers[AuthService.AUTHORIZATION_HEADER_NAME]!![0]

        waitToTokenExpired()

        mvc.perform(get("/api/v1/auth-check").header("Authorization", "Bearer $accessToken").cookie(*response.headers["Set-Cookie"]!!.map { testUtil.parseCookie(it) }.toTypedArray()))
            .andExpect(header().exists(AuthService.AUTHORIZATION_HEADER_NAME))
            .andExpect(cookie().exists(AuthService.REFRESH_TOKEN_COOKIE_NAME))

    }

    @DisplayName("인증 필터 - 실패")
    @Test
    fun fail() {
        val accessToken = "no.valid.access.token"

        mvc.perform(get("/api/v1/auth-check").header("Authorization", "Bearer $accessToken"))
            .andExpect(status().`is`(HttpStatus.UNAUTHORIZED.value()))
            .andExpect(header().doesNotExist(AuthService.AUTHORIZATION_HEADER_NAME))
            .andExpect(cookie().doesNotExist(AuthService.REFRESH_TOKEN_COOKIE_NAME))

    }

    @DisplayName("인증 필터 - 실패 (만료된 토큰, 새로고침토큰 없음)")
    @Test
    fun fail_expired_and_noRefreshToken() {
        val accessToken = testUtil.getAccessToken()

        waitToTokenExpired()

        mvc.perform(get("/api/v1/auth-check").header("Authorization", "Bearer $accessToken"))
            .andExpect(status().`is`(HttpStatus.UNAUTHORIZED.value()))
            .andExpect(header().doesNotExist(AuthService.AUTHORIZATION_HEADER_NAME))
            .andExpect(cookie().doesNotExist(AuthService.REFRESH_TOKEN_COOKIE_NAME))

    }

    @DisplayName("인증 필터 - 실패 (만료된 토큰, 새로고침토큰 틀림)")
    @Test
    fun fail_expired_and_invalidRefreshToken() {
        val accessToken = testUtil.getAccessToken()

        waitToTokenExpired()

        mvc.perform(get("/api/v1/auth-check").header("Authorization", "Bearer $accessToken").cookie(Cookie(AuthService.REFRESH_TOKEN_COOKIE_NAME, "invalidRefreshToken")))
            .andExpect(status().`is`(HttpStatus.UNAUTHORIZED.value()))
            .andExpect(header().doesNotExist(AuthService.AUTHORIZATION_HEADER_NAME))
            .andExpect(cookie().doesNotExist(AuthService.REFRESH_TOKEN_COOKIE_NAME))

    }

    @DisplayName("인증 필터 - 실패 (만료된 토큰, 새로고침토큰 키 다름)")
    @Test
    fun fail_expired_and_refreshToken_notMatches() {
        val accessToken = testUtil.getAccessToken()
        val response = testUtil.getAuthResponse()

        waitToTokenExpired()

        mvc.perform(get("/api/v1/auth-check").header("Authorization", "Bearer $accessToken").cookie(*response.headers["Set-Cookie"]!!.map { testUtil.parseCookie(it) }.toTypedArray()))
            .andExpect(status().`is`(HttpStatus.UNAUTHORIZED.value()))
            .andExpect(header().doesNotExist(AuthService.AUTHORIZATION_HEADER_NAME))
            .andExpect(cookie().doesNotExist(AuthService.REFRESH_TOKEN_COOKIE_NAME))

    }

    private fun waitToTokenExpired() {
        Thread.sleep((tokenValidityInSeconds + 1) * 1000)
    }

}