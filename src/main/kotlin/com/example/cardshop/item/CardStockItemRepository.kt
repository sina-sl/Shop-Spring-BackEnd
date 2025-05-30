package com.example.cardshop.item

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CardStockItemRepository : JpaRepository<CardStockItem, Long> {
    fun findByCardIdAndSoldFalseAndOrderIsNull(cardId: Long): List<CardStockItem>
    fun findTopByCardIdAndSoldFalseAndOrderIsNull(cardId: Long): Optional<CardStockItem>
} 