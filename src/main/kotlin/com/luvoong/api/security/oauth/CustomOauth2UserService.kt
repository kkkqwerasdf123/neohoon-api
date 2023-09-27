package com.luvoong.api.security.oauth

import com.luvoong.api.app.domain.member.Member
import com.luvoong.api.app.domain.member.MemberOauth
import com.luvoong.api.app.domain.member.MemberRole
import com.luvoong.api.app.domain.member.Role
import com.luvoong.api.app.repository.member.MemberOauthRepository
import com.luvoong.api.app.repository.member.MemberRepository
import com.luvoong.api.app.repository.member.MemberRoleRepository
import com.luvoong.api.security.oauth.attribute.KakaoAttribute
import com.luvoong.api.security.oauth.attribute.NaverAttribute
import com.luvoong.api.security.userdetails.UserInfo
import org.springframework.security.core.authority.SimpleGrantedAuthority
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
    private val memberOauthRepository: MemberOauthRepository,
): DefaultOAuth2UserService() {

    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {

        val user = super.loadUser(userRequest)

        val provider = Provider.valueOf(userRequest.clientRegistration.registrationId)

        val attribute = when (provider) {
            Provider.kakao -> KakaoAttribute(user.attributes)
            Provider.naver -> NaverAttribute(user.attributes)
        }

        val member = memberRepository.findByMemberOauthInfo(attribute.provider, attribute.providerId)
            ?: let {

                val member = Member(generateUsernameByProvider(provider), attribute.email).also { memberRepository.save(it) }
                val memberOauth = MemberOauth(member, provider, attribute.providerId).also { memberOauthRepository.save(it) }
                member.addOauth(memberOauth)

                val memberRole = MemberRole(member, Role.USER).also { memberRoleRepository.save(it) }
                member.roles.add(memberRole)
                member
            }


        return UserInfo(
            id = member.id!!,
            username = member.username,
            password = null,
            authorities = member.roles.map { SimpleGrantedAuthority(it.role.name) }.toMutableList(),
            attribute = attribute
        )
    }

    private fun generateUsernameByProvider(provider: Provider): String =
        "${UUID.randomUUID().toString().replace("-", "")}@${provider.name}"

}