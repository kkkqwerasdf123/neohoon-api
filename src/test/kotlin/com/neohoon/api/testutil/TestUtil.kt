package com.neohoon.api.testutil

import com.neohoon.api.app.domain.member.Member
import com.neohoon.api.app.domain.member.MemberRole
import com.neohoon.api.app.domain.member.Role
import com.neohoon.api.app.repository.member.MemberRepository
import com.neohoon.api.app.repository.member.MemberRoleRepository
import com.neohoon.api.security.service.AuthService
import jakarta.servlet.http.Cookie
import org.slf4j.LoggerFactory
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.time.LocalDate

class TestUtil {

    companion object {

        var member: Member? = null

    }

    private val log = LoggerFactory.getLogger(this::class.java)

    val username = "kkkqwerasdf123@naver.com"
    val password = "1234"

    var passwordEncoder = BCryptPasswordEncoder()
    lateinit var restTemplate: TestRestTemplate
    lateinit var memberRepository: MemberRepository
    lateinit var memberRoleRepository: MemberRoleRepository

    fun insertTestMember() {
        if (member != null) {
            return
        }
        val member = Member(username, username, "DHKIM", LocalDate.of(1991, 12, 17))
            .also {
                it.password = passwordEncoder.encode(password)
                memberRepository.save(it)
            }

        memberRoleRepository.saveAll(Role.values().map { MemberRole(member, it) })
        TestUtil.member = member
    }

    fun parseCookie(cookieString: String): Cookie {
        val rawCookieParams = cookieString.split(";")

        val rawCookieNameAndValue = rawCookieParams[0].split("=")
        val cookie = Cookie(rawCookieNameAndValue[0], rawCookieNameAndValue[1])

        for (i in 1 until rawCookieParams.size) {
            if (rawCookieParams[i].contains("=")) {
                val rawCookieParamNameAndValue = rawCookieParams[i].split("=")

                when (rawCookieParamNameAndValue[0].trim()) {
                    "Max-Age" -> cookie.maxAge = rawCookieParamNameAndValue[1].toInt()
                    "Path" -> cookie.path = rawCookieParamNameAndValue[1]
                }
            } else {
                when (rawCookieParams[i].trim()) {
                    "HttpOnly" -> cookie.isHttpOnly = true
                    "Secure" -> cookie.secure = true
                }
            }
        }
        return cookie
    }

    fun getAuthResponse(): ResponseEntity<Void> {
        return restTemplate.exchange("/api/v1/authenticate?username={0}&password={1}", HttpMethod.POST, RequestEntity.EMPTY, Void::class.java, username, password)
    }

    fun getAccessToken(): String {
        return restTemplate.exchange("/api/v1/authenticate?username={0}&password={1}", HttpMethod.POST, RequestEntity.EMPTY, Void::class.java, username, password).headers[AuthService.AUTHORIZATION_HEADER_NAME]!![0]
    }

    fun get(url: String): MockHttpServletRequestBuilder {
        val response = getAuthResponse()
        val accessToken = response.headers[AuthService.AUTHORIZATION_HEADER_NAME]!![0]
        return MockMvcRequestBuilders.get(url).header("Authorization", "Bearer $accessToken").cookie(*response.headers["Set-Cookie"]!!.map { parseCookie(it) }.toTypedArray())
    }

}