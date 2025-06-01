package com.example.cardshop.product

import com.example.cardshop.product.dto.ProductWithStockCountDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
) {

    @GetMapping
    fun searchActiveProducts(
        @RequestParam(required = false) search: String?,
        pageable: Pageable
    ): Page<ProductWithStockCountDto> = productService.searchActiveProducts(search ?: "", pageable)


    @GetMapping("/{id}")
    fun getActiveProduct(@PathVariable id: Long): ResponseEntity<ProductWithStockCountDto> {
        val product = productService.getActiveProductWithAvailableStockById(id)
        return if (product.isPresent) {
            ResponseEntity.ok(product.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
} 