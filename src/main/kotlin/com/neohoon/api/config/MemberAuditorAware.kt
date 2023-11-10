package com.neohoon.api.config

import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.*

@Component
class MemberAuditorAware: AuditorAware<Long> {
    override fun getCurrentAuditor(): Optional<Long> {
        return Optional.ofNullable(0L)
    }
}