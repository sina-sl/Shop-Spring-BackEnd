package com.example.cardshop.admin

import com.example.cardshop.product.*
import com.example.cardshop.product.dto.ProductWithStockCountDto
import com.example.cardshop.item.ProductStockItem
import com.example.cardshop.item.ProductStockItemRepository
import com.example.cardshop.order.Order
import com.example.cardshop.order.OrderRepository
import com.example.cardshop.order.OrderStatus
import com.example.cardshop.product.dto.ProductCreationData
import com.example.cardshop.product.dto.ProductUpdateData
import com.example.cardshop.item.dto.StockItemsCreationData
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class AdminService(
    private val productRepository: ProductRepository,
    private val productStockItemRepository: ProductStockItemRepository,
    private val orderRepository: OrderRepository
)  {
    // Admin method to get a product
    fun getProductById(productId: Long): Optional<Product> {
        return productRepository.findById(productId)
    }
    // Admin method to search products
    fun searchProducts(search: String, isActive: Boolean?, pageable: Pageable): Page<ProductWithStockCountDto> {
        return productRepository.searchActiveProductsWithAvailableStock(search, isActive, pageable)
    }
    // Admin method to create a new Product
    @Transactional
    fun createProduct(creationData: ProductCreationData): Product {
        return productRepository.save(
            Product(
                title = creationData.title,
                price = creationData.price,
                imageUrl = creationData.imageUrl,
                isActive = creationData.isActive,
                pricingType = creationData.pricingType,
                description = creationData.description,
                prepStatus = creationData.prepStatus,
                productType = creationData.productType,
                slug = creationData.code
            )
        )
    }
    // Admin method to add ProductStockItems to a Product
    @Transactional
    fun addStockItems(stockItemsCreationData: StockItemsCreationData): List<ProductStockItem> {
        val product = productRepository.findById(stockItemsCreationData.productId).orElseThrow { RuntimeException("Product not found") }
        val stockItems = (0..< stockItemsCreationData.count).map { ProductStockItem(product = product) }
        return productStockItemRepository.saveAll(stockItems)
    }
    fun getOrderById(orderId: Long): Optional<Order> {
        return orderRepository.findById(orderId)
    }
    fun searchOrders(status: OrderStatus, userId: Long, search: String, pageable: Pageable): Page<Order> {
        return orderRepository.findAllByFilter(
            search = search,
            pageable = pageable,
            status = status,
            userId = userId,
        )
    }
    @Transactional
    fun updateProduct(id: Long, updateData: ProductUpdateData): Product {
        val product = productRepository.findById(id).orElseThrow { RuntimeException("Product not found") }
        val updated = product.copy(
            title = updateData.title ?: product.title,
            price = updateData.price ?: product.price,
            slug = updateData.slug ?: product.slug,
            imageUrl = updateData.imageUrl ?: product.imageUrl,
            isActive = updateData.isActive ?: product.isActive,
            description = updateData.description ?: product.description,
            pricingType = updateData.pricingType ?: product.pricingType,
            prepStatus = updateData.prepStatus ?: product.prepStatus,
            productType = updateData.productType ?: product.productType
        )
        return productRepository.save(updated)
    }
}