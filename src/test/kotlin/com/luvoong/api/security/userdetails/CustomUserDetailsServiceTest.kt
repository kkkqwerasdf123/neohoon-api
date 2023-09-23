package com.luvoong.api.security.userdetails

import com.luvoong.api.app.domain.member.Role
import com.luvoong.api.app.repository.member.MemberRepository
import com.luvoong.api.app.repository.member.MemberRoleRepository
import com.luvoong.api.testutil.TestUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomUserDetailsServiceTest {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var memberRepository: MemberRepository
    @Autowired
    lateinit var memberRoleRepository: MemberRoleRepository
    val testUtil = TestUtil()

    lateinit var customUserDetailsService: CustomUserDetailsService

    @BeforeAll
    fun init() {

        log.info("beforeAll: detailsservice")

        testUtil.memberRepository = memberRepository
        testUtil.memberRoleRepository = memberRoleRepository
        testUtil.insertTestMember()
        customUserDetailsService = CustomUserDetailsService(memberRepository)
    }

    @DisplayName("loadUserByUsername")
    @Test
    fun loadUserByUsername() {
        //given
        val user = customUserDetailsService.loadUserByUsername(testUtil.username)

        //then
        assertEquals(testUtil.username, user.username)
        assertEquals(Role.values().size, user.authorities.size)

    }

    @DisplayName("createUserInfo")
    @Test
    fun createUserInfo() {
        //given
        val member = memberRepository.findById(TestUtil.member!!.id!!).orElseThrow()

        //when
        val userInfo =
            ReflectionTestUtils.invokeMethod<UserInfo>(customUserDetailsService, "createUserInfo", member)

        //then
        val expectedUserInfo = UserInfo(
            id = member.id!!,
            email = member.email,
            password = member.password,
            authorities = member.roles
                .map { SimpleGrantedAuthority("ROLE_${it.role.name}") }
                .toMutableList()
        )

        assertEquals(expectedUserInfo.id, userInfo!!.id)
        assertEquals(expectedUserInfo.username, userInfo.username)
        assertEquals(expectedUserInfo.password, userInfo.password)
        assertEquals(expectedUserInfo.authorities.size, userInfo.authorities.size)

    }


}
