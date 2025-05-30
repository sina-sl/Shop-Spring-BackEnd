package com.example.cardshop.payment

import com.example.cardshop.order.Order
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "payments")
data class Payment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: Order,

    val refId: String? = null,
    val authority: String,
    val status: PaymentStatus = PaymentStatus.PENDING,

    @Enumerated(EnumType.STRING)
    val paymentMethod: PaymentMethod = PaymentMethod.ZARINPAL,

    @Lob
    val rawResponse: String? = null,

    val paidAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)