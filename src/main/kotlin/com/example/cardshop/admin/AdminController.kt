package com.example.cardshop.admin

import com.example.cardshop.product.Product
import com.example.cardshop.product.dto.ProductWithStockCountDto
import com.example.cardshop.item.ProductStockItem
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
import com.example.cardshop.product.dto.ProductUpdateData
import com.example.cardshop.product.dto.ProductCreationData
import com.example.cardshop.item.dto.StockItemsCreationData

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
class AdminController(
    private val adminService: AdminService,
    private val orderService: OrderService,
    private val orderRepository: OrderRepository
) {

    // region Product Management
    @PostMapping("products/add")
    fun addProduct(@RequestBody req: ProductCreationData): ResponseEntity<Product> {
        val product = adminService.createProduct(req)
        return ResponseEntity.ok(product)
    }

    @PostMapping("products/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<Product> {
        val product = adminService.getProductById(id)
        return if (product.isPresent) {
            ResponseEntity.ok(product.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("products/{id}")
    fun updateProduct(@PathVariable id: Long, @RequestBody req: ProductUpdateData): ResponseEntity<Product> {
        val product = adminService.updateProduct(id, req)
        return ResponseEntity.ok(product)
    }

    @GetMapping("/products")
    fun searchProducts(
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) isActive: Boolean?,
        pageable: Pageable
    ): ResponseEntity<Page<ProductWithStockCountDto>> {
        val products = adminService.searchProducts(search ?: "", isActive, pageable)
        return ResponseEntity.ok(products)
    }
    // endregion

    // region Stock Management
    @PostMapping("/stocks/add")
    fun addStockItems(@RequestBody req: StockItemsCreationData): ResponseEntity<List<ProductStockItem>> {
        val stockItems = adminService.addStockItems(req)
        return ResponseEntity.ok(stockItems)
    }
    // endregion

    // region Order Management
    @GetMapping("/orders")
    fun searchOrders(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false, defaultValue = "") search: String,
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
    // endregion

}
