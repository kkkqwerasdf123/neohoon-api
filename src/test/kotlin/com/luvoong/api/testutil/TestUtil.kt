package com.luvoong.api.testutil

import com.luvoong.api.app.domain.member.Member
import com.luvoong.api.app.domain.member.MemberRole
import com.luvoong.api.app.domain.member.Role
import com.luvoong.api.app.repository.member.MemberRepository
import com.luvoong.api.app.repository.member.MemberRoleRepository
import com.luvoong.api.security.service.AuthService
import jakarta.servlet.http.Cookie
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.time.LocalDate

class TestUtil {

    companion object {

        var member: Member? = null

    }

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
        val member = Member("DH", "KIM", username, LocalDate.of(1991, 12, 17))
        member.password = passwordEncoder.encode(password)
        memberRepository.save(member)
        Role.values().map { MemberRole(member, it) }.forEach { memberRoleRepository.save(it) }
        TestUtil.member = member;
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
        return this.restTemplate.exchange("/api/v1/authenticate?username={0}&password={1}", HttpMethod.POST, null, Void::class.java, username, password)
    }

    fun getAccessToken(): String {
        return this.restTemplate.exchange("/api/v1/authenticate?username={0}&password={1}", HttpMethod.POST, null, Void::class.java, username, password).headers[AuthService.AUTHORIZATION_HEADER_NAME]!![0]
    }

    fun get(url: String): MockHttpServletRequestBuilder {
        val response = getAuthResponse()
        val accessToken = response.headers[AuthService.AUTHORIZATION_HEADER_NAME]!![0]
        return MockMvcRequestBuilders.get(url).header("Authorization", "Bearer $accessToken").cookie(*response.headers["Set-Cookie"]!!.map { parseCookie(it) }.toTypedArray())
    }

}