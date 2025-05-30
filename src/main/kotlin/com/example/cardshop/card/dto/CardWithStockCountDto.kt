package com.example.cardshop.card.dto

import com.example.cardshop.card.DeliveryType
import com.example.cardshop.card.PricingType

data class CardWithStockCountDto(
    val id: Long,
    val title: String,
    val price: Int,
    val imageUrl: String,
    val description: String,
    val pricingType: PricingType,
    val deliveryType: DeliveryType,
    val availableStockCount: Long
)