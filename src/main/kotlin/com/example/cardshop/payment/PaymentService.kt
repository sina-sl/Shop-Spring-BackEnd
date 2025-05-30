package com.example.cardshop.payment

import com.example.cardshop.order.Order
import com.example.cardshop.order.OrderService
import com.example.cardshop.order.OrderStatus
import com.example.cardshop.payment.provider.ZarinpalService
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface PaymentMethodsService {
    fun create(order: Order,  callbackUrl: String): CreatePaymentResponse?
    fun verify(order:Order, authority: String, status: String): PaymentVerifyResponse?
}

interface CreatePaymentResponse {
    val authority: String
    val paymentUrl: String
}

interface PaymentVerifyResponse {
    val isSuccess: Boolean
    val referenceId: String
    val message: String

    @get:JsonIgnore
    val rawData: String
}


@Service
class PaymentService(
    private val orderService: OrderService,
    private val zarinpalClient: ZarinpalService,
    private val paymentRepository: PaymentRepository,
    @Value("\${payment.callback-url}") private val callbackUrl: String
) {

    private val paymentMethodsService: Map<PaymentMethod, PaymentMethodsService> = mapOf(
        PaymentMethod.ZARINPAL to zarinpalClient
    )

    @Transactional
    fun createPayment(orderId: Long, userId: Long, paymentMethod: PaymentMethod): String {
        val order = orderService.getUserOrderById(orderId, userId).orElseThrow {
            RuntimeException("Order not found")
        }
        if (order.status != OrderStatus.JUST_CREATED) {
            throw IllegalStateException("Payment can only be initiated for orders with status JUST_CREATED.")
        }

        val response = paymentMethodsService[paymentMethod]?.let {
            it.create(order, callbackUrl+"?orderId=${order.id}")
                ?: throw RuntimeException("Failed to initiate zarinpal payment")
        } ?: throw RuntimeException("Payment method not supported")

        paymentRepository.save(
            Payment(
                order = order,
                authority = response.authority,
                paymentMethod = paymentMethod
            )
        )

        return response.paymentUrl
    }


    @Transactional
    fun verifyPayment(authority: String, status: String, paymentMethod: PaymentMethod,): PaymentVerifyResponse {

        val payment = paymentRepository.findByAuthorityAndPaymentMethodIs(authority,paymentMethod).orElseThrow {
            RuntimeException("Payment not found")
        }

        val order = payment.order
        if (order.status != OrderStatus.JUST_CREATED) {
            throw IllegalStateException("The order already paid")
        }

        val paymentMethodService = paymentMethodsService[paymentMethod]
            ?: throw RuntimeException("Payment method not supported")

        val paymentVerification = paymentMethodService.verify(order, authority, status)
            ?: throw RuntimeException("Payment verification failed")

        if (paymentVerification.isSuccess) {
            paymentRepository.save(
                payment.copy(
                    paidAt = LocalDateTime.now(),
                    status = PaymentStatus.SUCCESS,
                    refId = paymentVerification.referenceId,
                    rawResponse = paymentVerification.rawData
                )
            )

            orderService.onOrderPaid(order)
            return paymentVerification
        } else {
            throw RuntimeException("Payment verification failed")
        }
    }
}
