package com.luvoong.api.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders

@Configuration
class SpringDocsConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val info = Info()
            .title("TEST API DOCS")
            .version("0.1")
            .description("description")
            .contact(
                Contact().name("DongHyeok Kim").email("kkkqwerasdf123@naver.com")
                    .url("https://github.com/kkkqwerasdf123")
            )

        val bearerAuth = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .name(HttpHeaders.AUTHORIZATION)

        val securityRequirement = SecurityRequirement()
        securityRequirement.addList("JWT")

        return OpenAPI()
            .components(Components().addSecuritySchemes("JWT", bearerAuth))
            .addSecurityItem(securityRequirement)
            .info(info)

    }

}