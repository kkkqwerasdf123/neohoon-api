package com.luvoong.api.security.web

import com.luvoong.api.app.domain.member.Member
import com.luvoong.api.app.repository.member.MemberRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerTest {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    var mvc: MockMvc? = null

    @Autowired
    var memberRepository: MemberRepository? = null

    @Autowired
    var passwordEncoder: PasswordEncoder? = null

    @BeforeEach
    fun testMember() {
        val member = Member("DH", "KIM", "kkkqwerasdf123@naver.com", LocalDate.of(1991, 12, 17))
        member.password = passwordEncoder!!.encode("1234")
        memberRepository!!.save(member)
    }

    @DisplayName("로그인 - 성공")
    @Test
    fun authenticate() {


        mvc!!.perform(post("/api/v1/authenticate").param("username", "kkkqwerasdf123@naver.com").param("password", "1234"))
            .andExpect(status().isOk)
            .andExpect(header().exists("Authorization"))
            .andExpect(cookie().exists("lvrt"))

    }

    @DisplayName("로그인 - 성공2")
    @Test
    fun authenticate2() {

        mvc!!.perform(post("/api/v1/authenticate").param("username", "kkkqwerasdf123@naver.com").param("password", "1234"))
            .andExpect(status().isOk)
            .andExpect(header().exists("Authorization"))
            .andExpect(cookie().exists("lvrt"))

    }


}