package com.luvoong.api.config

import org.junit.jupiter.api.DisplayName
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@DisplayName("Redis Test Container")
@Configuration
class TestRedisContainerConfig {

    private val REDIS_DOCKER_IMAGE = "redis:latest"
    final var port: Int
    final var host: String

    init {
        val REDIS_CONTAINER = GenericContainer(DockerImageName.parse(REDIS_DOCKER_IMAGE))
            .withExposedPorts(6379)
            .withReuse(true)

        REDIS_CONTAINER.start()

        host = REDIS_CONTAINER.getHost()
        port = REDIS_CONTAINER.getMappedPort(6379)
    }

}