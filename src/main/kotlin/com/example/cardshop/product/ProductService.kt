package com.example.cardshop.product

import com.example.cardshop.product.dto.ProductWithStockCountDto
import com.example.cardshop.item.ProductStockItemRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val productStockItemRepository: ProductStockItemRepository
) {
    // Customer method to get a product
    fun getActiveProductById(productId: Long): Optional<Product> {
        return productRepository.findByIdAndIsActiveTrue(productId)
    }

    fun getActiveProductWithAvailableStockById(productId: Long): Optional<ProductWithStockCountDto> {
        return productRepository.findByIdAndIsActiveTrueWithAvailableStock(productId)
    }

    // Customer method to search active products
    fun searchActiveProducts(search: String, pageable: Pageable): Page<ProductWithStockCountDto> {
        return productRepository.searchActiveProductsWithAvailableStock(search, true, pageable)
    }

    // Customer method to handle the purchase of a Product
//    @Transactional
//    fun purchaseProduct(productId: Long): ProductStockItem {
//        val product = productRepository.findById(productId).orElseThrow { RuntimeException("Product not found") }
//        val availableItem = productStockItemRepository.findTopByProductAndSoldFalseAndOrderIsNull(product)
//            ?: throw RuntimeException("No available stock items for this product")
//
//        availableItem.sold = true
//        return productStockItemRepository.save(availableItem)
//    }
}
