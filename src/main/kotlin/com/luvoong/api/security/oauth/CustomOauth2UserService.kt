package com.luvoong.api.security.oauth

import com.luvoong.api.app.domain.member.Member
import com.luvoong.api.app.domain.member.MemberRole
import com.luvoong.api.app.domain.member.Role
import com.luvoong.api.app.repository.member.MemberRepository
import com.luvoong.api.app.repository.member.MemberRoleRepository
import com.luvoong.api.security.oauth.attribute.KakaoAttribute
import com.luvoong.api.security.userdetails.UserInfo
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CustomOauth2UserService(
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val passwordEncoder: PasswordEncoder
): DefaultOAuth2UserService() {

    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {

        val user = super.loadUser(userRequest)

        val provider = Provider.valueOf(userRequest.clientRegistration.registrationId)

        val attribute = when (provider) {
            Provider.kakao -> KakaoAttribute(user.attributes)
        }

        val email = attribute.email

        val member = memberRepository.findByEmailAndDeletedIsFalse(email)
            .orElseGet {
                val member = Member(generateNameByEmail(email), email)

                member.password = passwordEncoder.encode(UUID.randomUUID().toString())

                val memberRole = MemberRole(member, Role.USER)
                member.roles.add(memberRole)
                memberRepository.save(member)
                memberRoleRepository.save(memberRole)
                member
            }


        return UserInfo(
            id = member.id!!,
            email = member.email,
            password = null,
            authorities = member.roles.map { SimpleGrantedAuthority(it.role.name) }.toMutableList(),
            attribute = attribute
        )
    }

    private fun generateNameByEmail(email: String): String {
        val name = email.split("@")[0]
        return if (name.length > 30) name.substring(0..30) else name
    }

}