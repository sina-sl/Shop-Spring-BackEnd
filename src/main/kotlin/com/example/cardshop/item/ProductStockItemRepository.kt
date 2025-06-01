package com.example.cardshop.item

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductStockItemRepository : JpaRepository<ProductStockItem, Long> {
    fun findByProductIdAndSoldFalseAndOrderIsNull(productId: Long): List<ProductStockItem>
    fun findTopByProductIdAndSoldFalseAndOrderIsNull(productId: Long): Optional<ProductStockItem>
} 