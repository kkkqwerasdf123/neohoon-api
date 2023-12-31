package com.neohoon.api.app.domain.member

import com.neohoon.api.app.domain.BaseEntity
import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "member_role")
@EntityListeners(AuditingEntityListener::class)
class MemberRole(

    member: Member,
    role: Role,

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_role_id", nullable = false, updatable = false)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    val member: Member = member

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false, updatable = false)
    val role: Role = role

}