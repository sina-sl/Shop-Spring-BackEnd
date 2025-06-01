package com.example.cardshop.item

import com.example.cardshop.product.Product
import com.example.cardshop.order.Order
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "product_stock_items")
data class ProductStockItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    val product: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = true)
    @JsonIgnore
    val order: Order? = null,

    var sold: Boolean = false,

    val createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now()
) {

    // @Column(name = "reserve_time", nullable = true)
    // var reserveTime: java.time.LocalDateTime? = null

}