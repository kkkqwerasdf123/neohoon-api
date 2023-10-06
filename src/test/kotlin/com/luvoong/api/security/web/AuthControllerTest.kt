package com.luvoong.api.security.web

import com.epages.restdocs.apispec.*
import com.luvoong.api.app.repository.member.MemberRepository
import com.luvoong.api.app.repository.member.MemberRoleRepository
import com.luvoong.api.security.service.AuthService
import com.luvoong.api.testutil.TestUtil
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MockMvcBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter

@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension::class)
class AuthControllerTest {

    private val log = LoggerFactory.getLogger(this::class.java)

    lateinit var mvc: MockMvc
    @Autowired
    lateinit var ctx: WebApplicationContext
    @Autowired
    lateinit var restTemplate: TestRestTemplate
    @Autowired
    lateinit var memberRepository: MemberRepository
    @Autowired
    lateinit var memberRoleRepository: MemberRoleRepository

    val testUtil = TestUtil()

    @BeforeAll
    fun init() {

        log.info("restTemplate: {}", restTemplate)

        testUtil.restTemplate = restTemplate
        testUtil.memberRepository = memberRepository
        testUtil.memberRoleRepository = memberRoleRepository
        testUtil.insertTestMember()
    }

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        mvc = MockMvcBuilders.webAppContextSetup(ctx)
            .apply<DefaultMockMvcBuilder>(documentationConfiguration(restDocumentation))
            .build()
    }

    @DisplayName("로그인 - 성공")
    @Test
    fun authenticate_success() {

        mvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/authenticate")
                .param("username", testUtil.username)
                .param("password", testUtil.password)
        )
            .andDo(
                MockMvcRestDocumentationWrapper.document("authentication-docs",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .description("로그인")
                            .formParameters(
                                RequestDocumentation.parameterWithName("username").description("아이디"),
                                RequestDocumentation.parameterWithName("password").description("암호"),
                            )
                            .responseHeaders(
                                HeaderDocumentation.headerWithName("Authorization").description("JWT"),
                                HeaderDocumentation.headerWithName("Set-Cookie").description("Refresh Token Set-Cookie"),
                            )
                            .requestSchema(
                                Schema.schema("user")
                            )
                            .build()
                    ),
                )
            )
            .andExpect(status().isOk)
            .andExpect(header().exists(AuthService.AUTHORIZATION_HEADER_NAME))
            .andExpect(cookie().exists(AuthService.REFRESH_TOKEN_COOKIE_NAME))

    }

    @DisplayName("로그인 - 실패 - username")
    @Test
    fun authenticate_fail_username() {

        mvc.perform(post("/api/v1/authenticate").param("username", "nonusername").param("password", testUtil.password))
            .andExpect(status().is4xxClientError)
            .andExpect(header().doesNotExist(AuthService.AUTHORIZATION_HEADER_NAME))
            .andExpect(cookie().doesNotExist(AuthService.REFRESH_TOKEN_COOKIE_NAME))

    }

    @DisplayName("로그인 - 실패 - password")
    @Test
    fun authenticate2_fail_password() {

        mvc.perform(post("/api/v1/authenticate").param("username", testUtil.username).param("password", "nonpassword"))
            .andExpect(status().is4xxClientError)
            .andExpect(header().doesNotExist(AuthService.AUTHORIZATION_HEADER_NAME))
            .andExpect(cookie().doesNotExist(AuthService.REFRESH_TOKEN_COOKIE_NAME))

    }


}