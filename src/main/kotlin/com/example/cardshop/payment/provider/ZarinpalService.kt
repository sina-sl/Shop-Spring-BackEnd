package com.example.cardshop.payment.provider

import com.example.cardshop.order.Order
import com.example.cardshop.payment.CreatePaymentResponse
import com.example.cardshop.payment.PaymentMethodsService
import com.example.cardshop.payment.PaymentVerifyResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

// Payment Request
data class ZarinpalRequest(
    val merchant_id: String,
    val amount: Int,
    val callback_url: String,
    val description: String,
    // val metadata: Map<String, String>? = null
)

// Payment Request Response
data class ZarinpalRequestResponse(
    val data: ZarinpalRequestData,
    val errors: List<Any>?
)
data class ZarinpalRequestData(
    val code: Int,
    val message: String,
    val authority: String,
    val fee_type: String,
    val fee: Int?
)


// Payment Verify Request
data class ZarinpalVerifyRequest(
    val merchant_id: String,
    val amount: Int,
    val authority: String
)

// Payment Verify Response
data class ZarinpalVerifyResponse(
    val data: ZarinpalVerifyData,
    val errors: List<Any>
)

data class ZarinpalVerifyData(
    val code: Int,
    val message: String,
    val card_hash: String,
    val card_pan: String,
    val ref_id: Long,
    val fee_type: String,
    val fee: Int?
)


data class ZarinpalPaymentVerificationResponse(
    val response: ZarinpalVerifyResponse
) : PaymentVerifyResponse {

    override val rawData: String = response.toString()
    override val message: String = response.data.message
    override val referenceId: String = response.data.ref_id.toString()
    override val isSuccess: Boolean = response.data.code.let{ intArrayOf(100, 101).contains(it) }
}



@Component
class ZarinpalService(
    @Value("\${payment.providers.zarinpal.verify-url}") private val verifyUrl: String,
    @Value("\${payment.providers.zarinpal.merchant-id}") private val merchantId: String,
    @Value("\${payment.providers.zarinpal.request-url}") private val requestUrl: String,
    @Value("\${payment.providers.zarinpal.redirect-url}") private val redirectUrl: String
): PaymentMethodsService {
    private val restTemplate = RestTemplate()

    override fun create(order: Order, callbackUrl: String): CreatePaymentResponse {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            accept = listOf(MediaType.APPLICATION_JSON)
        }
        val body = ZarinpalRequest(
            merchant_id = merchantId,
            amount = order.totalPrice,
            callback_url = callbackUrl,
            description = "Payment for order ${order.id}",
        )

        val entity = HttpEntity(body, headers)
        val response = restTemplate.postForObject(requestUrl, entity, ZarinpalRequestResponse::class.java)
            ?: throw RuntimeException("Zarinpal payment failed!")

        return object : CreatePaymentResponse {
            override val authority: String = response.data.authority
            override val paymentUrl: String = "$redirectUrl/$authority"
        }
    }

    override fun verify(order: Order, authority: String, status: String): PaymentVerifyResponse {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            accept = listOf(MediaType.APPLICATION_JSON)
        }
        val body = ZarinpalVerifyRequest(
            amount = order.totalPrice,
            authority = authority,
            merchant_id = merchantId
        )
        val entity = HttpEntity(body, headers)
        val response = restTemplate.postForObject(verifyUrl, entity, ZarinpalVerifyResponse::class.java)
            ?: throw RuntimeException("Zarinpal verify payment failed!")

        return ZarinpalPaymentVerificationResponse(response)
    }
} 