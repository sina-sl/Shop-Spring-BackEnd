package com.example.cardshop.card

import com.example.cardshop.item.CardStockItem
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "cards")
data class Card(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val title: String,
    val price: Int,
    val imageUrl: String,

    @Column(length = 1000)
    val description: String,

    val isActive: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY)
    @JsonIgnore
    val stockItems: List<CardStockItem> = emptyList(),

    @Enumerated(EnumType.STRING)
    val pricingType: PricingType,

    @Enumerated(EnumType.STRING)
    val deliveryType: DeliveryType
)