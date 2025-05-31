package com.example.cardshop.card

import com.example.cardshop.card.dto.CardWithStockCountDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
//import java.awt.print.Pageable
import java.util.*

interface CardRepository : JpaRepository<Card, Long> {

    fun findByIdAndIsActiveTrue(@Param("id") id: Long): Optional<Card>


    @Query(
        """
            SELECT new com.example.cardshop.card.dto.CardWithStockCountDto(
                c.id, c.title, c.price, c.imageUrl, c.description, c.pricingType, c.deliveryType, COUNT(s.id)
            )
            FROM Card c
            LEFT JOIN c.stockItems s ON s.sold = false AND s.order IS NULL
            WHERE c.id = :id AND c.isActive = true
            GROUP BY c.id, c.title, c.price, c.imageUrl, c.description, c.pricingType, c.deliveryType
        """
    )
    fun findByIdAndIsActiveTrueWithAvailableStock(@Param("id") id: Long): Optional<CardWithStockCountDto>

    @Query(
        """
            SELECT new com.example.cardshop.card.dto.CardWithStockCountDto(c.id, c.title, c.price, c.imageUrl, c.description, c.pricingType, c.deliveryType, COUNT(s.id)) FROM Card c
            LEFT JOIN c.stockItems s ON s.sold = false AND s.order IS NULL
            WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))
            AND (:isActive IS NULL OR c.isActive = :isActive)
            GROUP BY c.id, c.title, c.price, c.imageUrl, c.description, c.pricingType, c.deliveryType
        """
    )
    fun searchActiveCardsWithAvailableStock(
        @Param("title") title: String,
        @Param("isActive") isActive: Boolean?,
        pageable: Pageable
    ): Page<CardWithStockCountDto>
}