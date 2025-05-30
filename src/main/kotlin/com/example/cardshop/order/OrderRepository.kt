package com.example.cardshop.order

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface OrderRepository : JpaRepository<Order, Long> {

    fun findByIdAndUserId(id: Long, userId: Long): Optional<Order>

    @Query(
        """
            SELECT o FROM Order o 
            WHERE (:userId IS NULL OR o.user.id = :userId) 
            AND (:status IS NULL OR o.status = :status)
            AND (:search IS NULL OR LOWER(o.card.title) LIKE LOWER(CONCAT('%', :search, '%')))
        """
    )
    fun findAllByFilter(
        @Param("userId") userId: Long?,
        @Param("status") status: OrderStatus?,
        @Param("search") search: String?,
        pageable: Pageable
    ): Page<Order>


//    fun findByAuthority(authority: String): Optional<Order>
} 