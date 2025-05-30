package com.example.cardshop.card

import com.example.cardshop.card.dto.CardWithStockCountDto
import com.example.cardshop.item.CardStockItemRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class CardService(
    private val cardRepository: CardRepository,
    private val cardStockItemRepository: CardStockItemRepository
) {
    // Customer method to get a card
    fun getActiveCardById(cardId: Long): Optional<Card> {
        return cardRepository.findByIdAndIsActiveTrue(cardId)
    }
    // Customer method to search active cards
    fun searchActiveCards(search: String, pageable: Pageable): Page<CardWithStockCountDto> {
        return cardRepository.searchActiveCardsWithAvailableStock(search,true, pageable)
    }

    // Customer method to handle the purchase of a Card
//    @Transactional
//    fun purchaseCard(cardId: Long): CardStockItem {
//        val card = cardRepository.findById(cardId).orElseThrow { RuntimeException("Card not found") }
//        val availableItem = cardStockItemRepository.findTopByCardAndSoldFalseAndOrderIsNull(card)
//            ?: throw RuntimeException("No available stock items for this card")
//
//        availableItem.sold = true
//        return cardStockItemRepository.save(availableItem)
//    }
}
