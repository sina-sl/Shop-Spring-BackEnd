package com.example.cardshop

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation::class)
class FlowIntegrationTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper
) {
    private val adminEmail = "admintest@example.com"
    private val adminPassword = "adminpass"
    private val userEmail = "testuser@example.com"
    private val userPassword = "userpass"
    private var cardId: Long = 0
    private var orderId: Long = 0

    private fun signupIfNeeded(email: String, password: String) {
        val signupReq = mapOf("email" to email, "password" to password)
        mockMvc.post("/auth/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(signupReq)
        }
    }

    private fun login(email: String, password: String): String {
        val loginReq = mapOf("email" to email, "password" to password)
        val result = mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginReq)
        }.andExpect { status { isOk() } }
         .andReturn()
        val json = objectMapper.readTree(result.response.contentAsString)
        return json.get("token").asText()
    }

    @Test
    @Order(1)
    fun `01_signup_users`() {
        signupIfNeeded(adminEmail, adminPassword)
        signupIfNeeded(userEmail, userPassword)
    }

    @Test
    @Order(2)
    fun `02_admin_creates_card`() {
        val adminToken = login(adminEmail, adminPassword)
        // Promote to admin if not already (direct DB or via endpoint if available)
        // For this test, assume the first user is admin or DB is clean
        val cardReq = mapOf(
            "title" to "Test Card",
            "description" to "A test card",
            "price" to 1000,
            "code" to UUID.randomUUID().toString(),
            "imageUrl" to "http://example.com/image.png"
        )
        val result = mockMvc.post("/admin/cards") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $adminToken")
            content = objectMapper.writeValueAsString(cardReq)
        }.andExpect { status { isOk() } }
         .andReturn()
        val json = objectMapper.readTree(result.response.contentAsString)
        cardId = json.get("id").asLong()
        assert(cardId > 0)
    }

    @Test
    @Order(3)
    fun `03_user_places_order_and_pays`() {
        val userToken = login(userEmail, userPassword)
        // Place order
        val orderReq = mapOf(
            "userId" to 2, // assuming testuser is id 2
            "cardId" to cardId,
            "quantity" to 1,
            "totalPrice" to 1000,
            "description" to "Buy test card"
        )
        val result = mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $userToken")
                .content(objectMapper.writeValueAsString(orderReq))
        ).andExpect(status().isOk)
         .andReturn()
        val json = objectMapper.readTree(result.response.contentAsString)
        orderId = json.get("orderId").asLong()
        assert(orderId > 0)
        // Simulate payment verification
        mockMvc.get("/orders/verify-payment") {
            param("Authority", "dummy-authority")
            param("Status", "OK")
        }
    }

    @Test
    @Order(4)
    fun `04_user_sees_card_in_orders`() {
        val userToken = login(userEmail, userPassword)
        val result = mockMvc.get("/orders/me") {
            header("Authorization", "Bearer $userToken")
        }.andExpect { status { isOk() } }
         .andReturn()
        val json = objectMapper.readTree(result.response.contentAsString)
        assert(json.isArray)
        assert(json.any { it.get("cardId")?.asLong() == cardId })
    }
} 