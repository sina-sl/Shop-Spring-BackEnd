package com.example.cardshop.product

import com.example.cardshop.item.ProductStockItem
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener::class)
class Product(
    val price: Int,
    val slug: String,
    val title: String,
    val imageUrl: String,
    @Column(length = 1000)
    val description: String,
    val isActive: Boolean = false,
    @Enumerated(EnumType.STRING)
    val prepStatus: PrepStatus,
    @Enumerated(EnumType.STRING)
    val pricingType: PricingType,
    @Enumerated(EnumType.STRING)
    val productType: ProductType
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @CreatedDate
    val createdAt: LocalDateTime? = null

    @LastModifiedDate
    val updatedAt: LocalDateTime? = null

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonIgnore
    val stockItems: List<ProductStockItem> = emptyList()
}