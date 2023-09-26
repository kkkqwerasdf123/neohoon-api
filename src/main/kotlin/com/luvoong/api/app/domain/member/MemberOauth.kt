package com.luvoong.api.app.domain.member

import com.luvoong.api.app.domain.BaseEntity
import com.luvoong.api.security.oauth.Provider
import jakarta.persistence.*

@Entity
@Table(name = "member_oauth")
class MemberOauth(
    member: Member,
    provider: Provider,
    providerId: String
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_oauth_id", nullable = false, updatable = false)
    var id: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false, updatable = false)
    val provider: Provider = provider

    @Column(nullable = false, updatable = false)
    val providerId: String = providerId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    val member: Member = member
}