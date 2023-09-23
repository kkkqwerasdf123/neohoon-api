package com.luvoong.api.security.web

import com.luvoong.api.app.repository.member.MemberRepository
import com.luvoong.api.app.repository.member.MemberRoleRepository
import com.luvoong.api.security.service.AuthService
import com.luvoong.api.testutil.TestUtil
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerTest {

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

    @BeforeAll
    fun init() {

        log.info("restTemplate: {}", restTemplate)

        testUtil.restTemplate = restTemplate
        testUtil.memberRepository = memberRepository
        testUtil.memberRoleRepository = memberRoleRepository
        testUtil.insertTestMember()
    }

    @DisplayName("로그인 - 성공")
    @Test
    fun authenticate_success() {

        mvc.perform(post("/api/v1/authenticate").param("username", testUtil.username).param("password", testUtil.password))
            .andExpect(status().isOk)
            .andExpect(header().exists(AuthService.AUTHORIZATION_HEADER_NAME))
            .andExpect(cookie().exists(AuthService.REFRESH_TOKEN_COOKIE_NAME))

    }

    @DisplayName("로그인 - 실패 - username")
    @Test
    fun authenticate_fail_username() {

        mvc.perform(post("/api/v1/authenticate").param("username", "nonusername").param("password", testUtil.password))
            .andExpect(status().is4xxClientError)
            .andExpect(header().doesNotExist(AuthService.AUTHORIZATION_HEADER_NAME))
            .andExpect(cookie().doesNotExist(AuthService.REFRESH_TOKEN_COOKIE_NAME))

    }

    @DisplayName("로그인 - 실패 - password")
    @Test
    fun authenticate2_fail_password() {

        mvc.perform(post("/api/v1/authenticate").param("username", testUtil.username).param("password", "nonpassword"))
            .andExpect(status().is4xxClientError)
            .andExpect(header().doesNotExist(AuthService.AUTHORIZATION_HEADER_NAME))
            .andExpect(cookie().doesNotExist(AuthService.REFRESH_TOKEN_COOKIE_NAME))

    }


}