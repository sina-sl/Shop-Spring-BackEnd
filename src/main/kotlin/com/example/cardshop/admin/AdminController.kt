package com.example.cardshop.admin

import com.example.cardshop.card.Card
import com.example.cardshop.card.dto.CardWithStockCountDto
import com.example.cardshop.item.CardStockItem
import com.example.cardshop.order.Order
import com.example.cardshop.order.OrderRepository
import com.example.cardshop.order.OrderService
import com.example.cardshop.order.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
class AdminController(
    private val adminService: AdminService,
    private val orderService: OrderService,
    private val orderRepository: OrderRepository
) {

    @PostMapping("cards/add")
    fun addCard(@RequestBody req: CardCreationData): ResponseEntity<Card> {
        val card = adminService.createCard(req)
        return ResponseEntity.ok(card)
    }

    @PostMapping("cards/{id}")
    fun getCardById(@PathVariable id: Long): ResponseEntity<Card> {
        val card = adminService.getCardById(id)
        return if (card.isPresent) {
            ResponseEntity.ok(card.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/cards")
    fun searchCards(
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) isActive: Boolean?,
        pageable: Pageable
    ): ResponseEntity<Page<CardWithStockCountDto>> {
        val cards = adminService.searchCards(search?:"",isActive, pageable)
        return ResponseEntity.ok(cards)
    }

    
    @PostMapping("/stocks/add")
    fun addStockItems(@RequestBody req: StockItemsCreationData): ResponseEntity<List<CardStockItem>> {
        val stockItems = adminService.addStockItems(req)
        return ResponseEntity.ok(stockItems)
    }




    @GetMapping("/orders")
    fun searchOrders(
        @RequestParam(required = false) userId: Long,
        @RequestParam(defaultValue = "") search: String,
        @RequestParam(required = false) status: OrderStatus?,
        @PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): Page<Order> {
        return orderRepository.findAllByFilter(
            userId,
            status,
            search,
            pageable
        )
    }

    @GetMapping("/orders/{id}")
    fun getOrderById(@PathVariable id: Long): ResponseEntity<Order> {
        val order = orderRepository.findById(id)
        return if (order.isPresent)
            ResponseEntity.ok(order.get())
        else
            ResponseEntity.notFound().build()
    }

//    @PatchMapping("/admin/{id}")
//    fun updateOrderStatus(@PathVariable id: Long, @RequestBody req: UpdateOrderStatusRequest): ResponseEntity<Order> {
//        val orderOpt = orderService.getOrderById(id)
//        if (orderOpt.isEmpty) return ResponseEntity.notFound().build()
//        val order = orderOpt.get()
//        // order.status = req.status
////        orderRepository.save(order)
//        // do in service
//        return ResponseEntity.ok(order)
//    }
}
