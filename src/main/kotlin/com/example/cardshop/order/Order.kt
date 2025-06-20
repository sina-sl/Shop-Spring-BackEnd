package com.example.cardshop.order

import com.example.cardshop.product.Product
import com.example.cardshop.item.ProductStockItem
import com.example.cardshop.payment.Payment
import com.example.cardshop.user.User
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    val user: User,

    val quantity: Int,

    @Enumerated(EnumType.STRING)
    val status: OrderStatus = OrderStatus.JUST_CREATED,
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonIgnore
    val productStockItems: List<ProductStockItem> = emptyList(),

    val preparationNote: String? = null,
    val deliveryDate: LocalDateTime? = null,

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val payments: List<Payment> = emptyList(),
    val purchaseDate: LocalDateTime? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

) {
    @get:Transient
    val totalPrice: Int
        get() = product.price * quantity
}