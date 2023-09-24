package com.luvoong.api.app.domain.member

import com.luvoong.api.app.domain.BaseEntity
import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate

@Entity
@Table(name = "member")
@EntityListeners(AuditingEntityListener::class)
class Member (

    name: String,
    email: String,
    birthday: LocalDate? = null

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false, updatable = false)
    var id: Long? = null

    @Column(name = "email", nullable = false, updatable = false, unique = true, length = 320)
    var email: String = email

    @Column(nullable = false, length = 30)
    var name: String = name

    var password: String? = null

    var birthday: LocalDate? = birthday


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    var roles: MutableCollection<MemberRole> = mutableListOf()

}