package com.example.cardshop.order

import com.example.cardshop.user.CustomUserDetails
import com.example.cardshop.user.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService,
    private val userService: UserService
) {

    @GetMapping("/{id}")
    fun getOrderById(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<Order> {
        val order = orderService.getUserOrderById(id, principal.id)
        return if (order.isPresent) ResponseEntity.ok(order.get()) else ResponseEntity.notFound().build()
    }

    @GetMapping
    fun searchUserOrders(
        @AuthenticationPrincipal principal: CustomUserDetails,
        @RequestParam(required = false) status: OrderStatus?,
        @RequestParam(required = false, defaultValue = "") search: String?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<Order>> {
        val orders = orderService.searchUserOrders(
            userId = principal.id,
            status = status,
            search = search,
            pageable = pageable
        )
        return ResponseEntity.ok(orders)
    }



    data class PlaceOrderRequest(
        val cardId: Long
    )

    @PostMapping("/create")
    fun createOrder(
        @RequestBody req: PlaceOrderRequest,
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<Order> {
        val order = orderService.createOrder(principal.id, req.cardId)
        return ResponseEntity.ok(order)
    }
} 