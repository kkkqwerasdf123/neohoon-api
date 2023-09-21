package com.luvoong.api.security.authentication


import com.luvoong.api.security.authentication.TokenValidateState.*
import com.luvoong.api.security.userdetails.UserInfo
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.*

@Component
class TokenProvider(

    @Value("\${luvoong.auth.jwt.secret}")
    secret: String,

    @Value("\${luvoong.auth.jwt.validity-in-seconds}")
    tokenValidityInSeconds: Long,

) {

    private val AUTHORITIES_NAME = "auth"
    private val ID_NAME = "id"
    private val REFRESH_KEY_NAME = "lvrk"
    private val key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
    private val tokenValidityInMilliSeconds: Long = tokenValidityInSeconds * 1000

    private val log = LoggerFactory.getLogger(this::class.java)


    fun createToken(user: UserInfo): String {

        val now = Date()

        return Jwts.builder()
            .setSubject(user.username)
            .claim(ID_NAME, user.id)
            .claim(AUTHORITIES_NAME, user.authorities.map { it.authority }.joinToString(","))
            .claim(REFRESH_KEY_NAME, user.key)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + tokenValidityInMilliSeconds))
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        val authorities = extractAuthorityFromClaims(claims)

        val principal = UserInfo(
            id = claims.get(ID_NAME, Integer::class.java).toLong(),
            email = claims.subject,
            password = null,
            authorities = authorities
        )

        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun accessTokenValidState(token: String): TokenValidateState {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            log.debug("valid token")
            VALID;
        } catch (e: ExpiredJwtException) {
            log.debug("expired token : {}", e.message)
            EXPIRED
        } catch (e: Exception) {
            log.debug("invalid token : ", e)
            INVALID
        }
    }

    fun getUserOfExpiredToken(jwt: String): UserInfo {
        return try {
            val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJwt(jwt).body
            UserInfo(claims.get(ID_NAME, Integer::class.java).toLong(), claims.subject, null, extractAuthorityFromClaims(claims), claims.get(REFRESH_KEY_NAME, String::class.java))
        } catch (e: ExpiredJwtException) {
            UserInfo(e.claims.get(ID_NAME, Integer::class.java).toLong(), e.claims.subject, null, extractAuthorityFromClaims(e.claims), e.claims.get(REFRESH_KEY_NAME, String::class.java))
        }
    }

    fun extractAuthorityFromClaims(claims: Claims): MutableCollection<out GrantedAuthority> {
        return claims.get(AUTHORITIES_NAME).toString().split(",")
            .filter { StringUtils.hasText(it) }
            .map { SimpleGrantedAuthority(it) }
            .toMutableList()
    }

}