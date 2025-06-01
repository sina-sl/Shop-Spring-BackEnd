package com.example.cardshop.product.dto

import com.example.cardshop.product.PricingType
import com.example.cardshop.product.PrepStatus
import com.example.cardshop.product.ProductType

data class ProductCreationData(
    val title: String,
    val price: Int,
    val code: String,
    val imageUrl: String,
    val isActive: Boolean,
    val description: String,
    val pricingType: PricingType,
    val prepStatus: PrepStatus,
    val productType: ProductType
) 