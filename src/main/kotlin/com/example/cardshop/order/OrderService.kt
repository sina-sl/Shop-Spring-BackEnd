package com.example.cardshop.order

import com.example.cardshop.product.Product
import com.example.cardshop.product.ProductService
import com.example.cardshop.product.PrepStatus.PREPARATION_REQUIRED
import com.example.cardshop.product.PrepStatus.READY_TO_SHIP
import com.example.cardshop.item.ProductStockItemRepository
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
    private val productService: ProductService,
    private val orderRepository: OrderRepository,
    private val productStockItemRepository: ProductStockItemRepository
) {

    @Transactional
    fun createOrder(userId: Long, productId: Long): Order {
        val user = userService.getUserProfile(userId).orElseThrow { RuntimeException("User not found") }
        val product = productService.getActiveProductById(productId).orElseThrow { RuntimeException("Product not found") }

        return when (product.prepStatus) {
            READY_TO_SHIP -> createReadyToShipOrder(user, product)
            PREPARATION_REQUIRED -> createPreparationRequiredOrder(user, product)
        }
    }

    private fun createReadyToShipOrder(user: User, product: Product): Order {
        val availableItem = productStockItemRepository.findTopByProductIdAndSoldFalseAndOrderIsNull(product.id).orElseThrow {
            RuntimeException("No available stock items for this product")
        }

        // Create the order in PENDING status, linking to the reserved item
        val order = Order(
            user = user,
            product = product,
            quantity = 1, // Always 1 for unique items
            status = OrderStatus.JUST_CREATED
        )

        val stockItem = availableItem.copy(order = order,)

        orderRepository.save(order)
        productStockItemRepository.save(stockItem)
        
        return order
    }
    
    private fun createPreparationRequiredOrder(user: User, product: Product): Order {
        return orderRepository.save(
            Order(
                user = user,
                product = product,
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
        when (order.product.prepStatus) {
            READY_TO_SHIP -> onReadyToShipProductPaid(order)
            PREPARATION_REQUIRED -> onPreparationRequiredProductPaid(order)
        }
    }

    private fun onReadyToShipProductPaid(order: Order): Order {
        val newOrder = orderRepository.save(
            order.copy(
                status = OrderStatus.COMPLETED,
                purchaseDate = LocalDateTime.now(),
                deliveryDate = LocalDateTime.now()
            )
        )

        productStockItemRepository.saveAll(
            newOrder.productStockItems.map {
                it.copy(sold = true)
            }
        )

        return newOrder
    }

    private fun onPreparationRequiredProductPaid(order: Order): Order {
        return orderRepository.save(
            order.copy(
                status = OrderStatus.PROGRESSING,
                purchaseDate = LocalDateTime.now()
            )
        )
    }
}