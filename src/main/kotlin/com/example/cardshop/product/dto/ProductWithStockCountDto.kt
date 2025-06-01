package com.example.cardshop.product.dto

import com.example.cardshop.product.PrepStatus
import com.example.cardshop.product.PricingType
import com.example.cardshop.product.ProductType
import java.time.LocalDateTime

data class ProductWithStockCountDto(
    val id: Long,
    val price: Int,
    val slug: String,
    val title: String,
    val imageUrl: String,
    val isActive: Boolean,
    val description: String,
    val prepStatus: PrepStatus,
    val pricingType: PricingType,
    val productType: ProductType,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val availableStockCount: Long
)