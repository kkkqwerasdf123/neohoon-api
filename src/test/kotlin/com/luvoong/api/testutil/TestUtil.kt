package com.luvoong.api.testutil

import com.luvoong.api.security.service.AuthService
import jakarta.servlet.http.Cookie
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

class TestUtil {

    private val username = "kkkqwerasdf123@naver.com"
    private val password = "1234"

    var restTemplate: TestRestTemplate? = null

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
        return restTemplate!!.exchange("/api/v1/authenticate?username={0}&password={1}", HttpMethod.POST, null, Void::class.java, username, password)
    }

    fun getAccessToken(): String {
        return restTemplate!!.exchange("/api/v1/authenticate?username={0}&password={1}", HttpMethod.POST, null, Void::class.java, username, password).headers[AuthService.AUTHORIZATION_HEADER_NAME]!![0]
    }

    fun get(url: String): MockHttpServletRequestBuilder {
        val response = getAuthResponse()
        val accessToken = response.headers[AuthService.AUTHORIZATION_HEADER_NAME]!![0]
        return MockMvcRequestBuilders.get(url).header("Authorization", "Bearer $accessToken").cookie(*response.headers["Set-Cookie"]!!.map { parseCookie(it) }.toTypedArray())
    }

}