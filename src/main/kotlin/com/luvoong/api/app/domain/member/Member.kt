package com.luvoong.api.app.domain.member

import com.luvoong.api.app.domain.BaseEntity
import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate

@Entity
@Table(name = "member")
@EntityListeners(AuditingEntityListener::class)
class Member (

    username: String,
    email: String?,
    name: String? = null,
    birthday: LocalDate? = null

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false, updatable = false)
    var id: Long? = null

    @Column(name = "username", nullable = false, updatable = false, unique = true, length = 320)
    val username: String = username

    @Column(name = "email", length = 320)
    var email: String? = email

    @Column(length = 30)
    var name: String? = name

    var password: String? = null

    var birthday: LocalDate? = birthday


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    var roles: MutableCollection<MemberRole> = mutableListOf()

    @OneToMany(fetch = FetchType.LAZY)
    var memberOauth: MutableList<MemberOauth> = mutableListOf()

    fun addOauth(memberOauth: MemberOauth) {
        this.memberOauth.add(memberOauth)
    }

}