package com.luvoong.api.config

import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JasyptConfig (
    @Value("\${jasypt.encryptor.password:rlaehdgurWkd}")
    var password: String,
    @Value("\${jasypt.encryptor.algorithm}")
    val algorithm: String,
    @Value("\${jasypt.encryptor.key-obtention-iterations}")
    val keyObtentionIterations: Int,
    @Value("\${jasypt.encryptor.pool-size}")
    val poolSize: Int,
    @Value("\${jasypt.encryptor.string-output-type}")
    val outputType: String
) {

    @Bean("jasyptStringEncryptor")
    fun stringEncryptor(): StringEncryptor {
        SimpleStringPBEConfig()
            .let {
                it.password = password
                it.algorithm = algorithm
                it.keyObtentionIterations = keyObtentionIterations
                it.poolSize = poolSize
                it.stringOutputType = outputType

                password = ""

                val encryptor = PooledPBEStringEncryptor()
                encryptor.setConfig(it)
                return encryptor
            }
    }


}