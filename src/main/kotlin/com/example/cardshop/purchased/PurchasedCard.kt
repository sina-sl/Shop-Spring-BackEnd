package com.example.cardshop.purchased

//@Entity
//@Table(name = "purchased_cards")
//data class PurchasedCard(
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    val id: Long = 0,
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    val user: User,
//
//    val purchaseDate: LocalDateTime = LocalDateTime.now(),
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "order_id")
//    val order: Order
//)