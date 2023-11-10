package com.neohoon.api.app.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Embeddable
@MappedSuperclass
class BaseEntity {

    @CreatedBy
    var createdBy: Long? = null

    @CreatedDate
    var createdDate: LocalDateTime? = null

    @LastModifiedBy
    var lastModifiedBy: Long? = null

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime? = null

    @Column(nullable = false)
    var deleted = false

}