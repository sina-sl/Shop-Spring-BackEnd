package com.example.cardshop.order

import com.example.cardshop.card.Card
import com.example.cardshop.card.CardService
import com.example.cardshop.card.DeliveryType.PREPARATION_REQUIRED
import com.example.cardshop.card.DeliveryType.READY_TO_SHIP
import com.example.cardshop.item.CardStockItemRepository
import com.example.cardshop.payment.PaymentService
import com.example.cardshop.user.User
import com.example.cardshop.user.UserService
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class OrderService(
    private val userService: UserService,
    private val cardService: CardService,
    private val orderRepository: OrderRepository,
    private val cardStockItemRepository: CardStockItemRepository
) {

    @Transactional
    fun createOrder(userId: Long, cardId: Long): Order {
        val user = userService.getUserProfile(userId).orElseThrow { RuntimeException("User not found") }
        val card = cardService.getActiveCardById(cardId).orElseThrow { RuntimeException("Card not found") }

        return when (card.deliveryType) {
            READY_TO_SHIP -> createReadyToShipOrder(user, card)
            PREPARATION_REQUIRED -> createPreparationRequiredOrder(user, card)
        }
    }

    private fun createReadyToShipOrder(user: User, card: Card): Order {
        val availableItem = cardStockItemRepository.findTopByCardIdAndSoldFalseAndOrderIsNull(card.id).orElseThrow {
            RuntimeException("No available stock items for this card")
        }

        // Create the order in PENDING status, linking to the reserved item
        val order = Order(
            user = user,
            card = card,
            quantity = 1, // Always 1 for unique items
            status = OrderStatus.JUST_CREATED
        )

        val stockItem = availableItem.copy(order = order,)

        orderRepository.save(order)
        cardStockItemRepository.save(stockItem)
        
        return order
    }
    
    private fun createPreparationRequiredOrder(user: User, card: Card): Order {
        return orderRepository.save(
            Order(
                user = user,
                card = card,
                quantity = 1, // Always 1 for unique items
                status = OrderStatus.JUST_CREATED
            )
        )
    }

    fun getUserOrderById(orderId: Long, userId: Long): Optional<Order> {
        return orderRepository.findByIdAndUserId(orderId, userId)
    }

    fun searchUserOrders(
        userId: Long,
        pageable: Pageable,
        status: OrderStatus?,
        search: String?
    ): Page<Order> {
        return orderRepository.findAllByFilter(
            userId = userId,
            status = status,
            search = search,
            pageable = pageable,
        )
    }

    @Transactional
    fun onOrderPaid(order: Order) {
        when (order.card.deliveryType) {
            READY_TO_SHIP -> onReadyToShipCardPaid(order)
            PREPARATION_REQUIRED -> onPreparationRequiredCardPaid(order)
        }
    }

    private fun onReadyToShipCardPaid(order: Order): Order {
        val newOrder = orderRepository.save(
            order.copy(
                status = OrderStatus.COMPLETED,
                purchaseDate = LocalDateTime.now(),
                deliveryDate = LocalDateTime.now()
            )
        )

        cardStockItemRepository.saveAll(
            newOrder.cardStockItems.map {
                it.copy(sold = true)
            }
        )

        return newOrder
    }

    private fun onPreparationRequiredCardPaid(order: Order): Order {
        return orderRepository.save(
            order.copy(
                status = OrderStatus.PROGRESSING,
                purchaseDate = LocalDateTime.now()
            )
        )
    }
}