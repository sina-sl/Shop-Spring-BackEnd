package com.example.cardshop.payment

import com.example.cardshop.user.CustomUserDetails
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/payment")
class PaymentController (
  private val paymentService: PaymentService
) {

    @GetMapping("/verify")
    fun verifyPayment(
        @RequestParam authority: String,
        @RequestParam status: String,
        @RequestParam orderId: Long
    ): ResponseEntity<PaymentVerifyResponse> {
        val result = paymentService.verifyPayment(
            status = status,
            authority = authority,
            paymentMethod = PaymentMethod.ZARINPAL,
        )
        return ResponseEntity.ok(result)
    }

    @PostMapping("/create")
    fun createPayment(
        @RequestParam orderId: Long,
        @RequestParam paymentMethod: PaymentMethod,
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<String> {
        val paymentUrl = paymentService.createPayment(
            orderId,
            principal.id,
            paymentMethod
        )
        return ResponseEntity.ok(paymentUrl)
    }
}