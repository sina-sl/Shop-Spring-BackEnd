package com.example.cardshop.payment

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PaymentRepository: JpaRepository<Payment, Long> {
    fun findByAuthorityAndPaymentMethodIs(authority: String, paymentMethod: PaymentMethod): Optional<Payment>
}