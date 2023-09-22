package com.luvoong.api.security.authentication

import com.luvoong.api.app.domain.member.Role
import com.luvoong.api.app.repository.member.MemberRepository
import com.luvoong.api.app.repository.member.MemberRoleRepository
import com.luvoong.api.security.userdetails.CustomUserDetailsService
import com.luvoong.api.security.userdetails.UserInfo
import com.luvoong.api.testutil.TestUtil
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomAuthenticationProviderTest {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var memberRepository: MemberRepository
    @Autowired
    lateinit var memberRoleRepository: MemberRoleRepository
    val testUtil = TestUtil()

    lateinit var customAuthenticationProvider: CustomAuthenticationProvider

    @BeforeAll
    fun init() {

        log.info("beforeAll: provider")

        testUtil.memberRepository = memberRepository
        testUtil.memberRoleRepository = memberRoleRepository
        testUtil.insertTestMember()
        customAuthenticationProvider = CustomAuthenticationProvider(CustomUserDetailsService(memberRepository), BCryptPasswordEncoder())
    }

    @DisplayName("authenticate")
    @Test
    fun authenticate() {
        //given
        val authentication =
            UsernamePasswordAuthenticationToken(testUtil.username, testUtil.password)

        //when
        val authenticateResult = customAuthenticationProvider.authenticate(authentication)

        //then
        assertEquals(UserInfo::class.java, authenticateResult.principal::class.java)
        assertNull(authenticateResult.credentials)
        assertEquals(Role.values().size, authenticateResult.authorities.size)

    }

    @DisplayName("support")
    @Test
    fun support() {
        //given
        //when
        val support = customAuthenticationProvider.supports(UsernamePasswordAuthenticationToken::class.java)

        //then
        assertTrue(support)

    }


    @DisplayName("getAuthenticationByUsername")
    @Test
    fun getAuthenticationByUsername() {
        //given
        //when
        val authenticationByUsername = customAuthenticationProvider.getAuthenticationByUsername(testUtil.username)

        //then

        assertEquals(testUtil.username, (authenticationByUsername.principal as UserInfo).username)
        assertEquals(TestUtil.member!!.id, (authenticationByUsername.principal as UserInfo).id)
        assertNotEquals(0, (authenticationByUsername.principal as UserInfo).authorities.size)
    }

}