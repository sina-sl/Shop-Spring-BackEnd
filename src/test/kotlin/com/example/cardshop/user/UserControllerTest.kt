package com.example.cardshop.user

import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.core.userdetails.User
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDateTime
import com.example.cardshop.config.SecurityConfig
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.FilterType

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest @Autowired constructor(
    val mockMvc: MockMvc
) {
    @MockBean
    lateinit var userRepository: UserRepository
    @MockBean
    lateinit var passwordEncoder: org.springframework.security.crypto.password.PasswordEncoder

//    @Test
//    @WithMockUser(username = "test@example.com", roles = ["USER"])
//    fun `should return user profile for authenticated user`() {
//        val user = com.example.cardshop.user.User(
//            id = 1L,
//            email = "test@example.com",
//            passwordHash = "hashed",
//            role = UserRole.USER,
//            createdAt = LocalDateTime.now(),
//            provider = "local"
//        )
//        Mockito.`when`(userRepository.findByEmail("test@example.com")).thenReturn(user)
//
//        mockMvc.get("/me")
//            .andExpect {
//                status { isOk() }
//                jsonPath("$.email") { value("test@example.com") }
//                jsonPath("$.role") { value("USER") }
//            }
//    }
} 