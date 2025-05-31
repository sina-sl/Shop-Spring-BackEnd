package com.example.cardshop.admin

import com.example.cardshop.card.Card
import com.example.cardshop.card.CardRepository
import com.example.cardshop.card.DeliveryType
import com.example.cardshop.card.PricingType
import com.example.cardshop.card.dto.CardWithStockCountDto
import com.example.cardshop.item.CardStockItem
import com.example.cardshop.item.CardStockItemRepository
import com.example.cardshop.order.Order
import com.example.cardshop.order.OrderRepository
import com.example.cardshop.order.OrderStatus
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class AdminService(
    private val cardRepository: CardRepository,
    private val cardStockItemRepository: CardStockItemRepository,

    private val orderRepository: OrderRepository
)  {

    // Admin method to get a card
    fun getCardById(cardId: Long): Optional<Card> {
        return cardRepository.findById(cardId)
    }
    // Admin method to search cards
    fun searchCards(search: String, isActive: Boolean?, pageable: Pageable): Page<CardWithStockCountDto> {
        return cardRepository.searchActiveCardsWithAvailableStock(search, isActive, pageable)
    }
    // Admin method to create a new Card
    @Transactional
    fun createCard(creationData: CardCreationData): Card {
        return cardRepository.save(
            Card(
                title = creationData.title,
                price = creationData.price,
                imageUrl = creationData.imageUrl,
                isActive = creationData.isActive,
                pricingType = creationData.pricingType,
                description = creationData.description,
                deliveryType = creationData.deliveryType,
            )
        )
    }


    // Admin method to add CardStockItems to a Card
    @Transactional
    fun addStockItems(stockItemsCreationData: StockItemsCreationData): List<CardStockItem> {
        val card = cardRepository.findById(stockItemsCreationData.cardId).orElseThrow { RuntimeException("Card not found") }
        val stockItems = (0..< stockItemsCreationData.count).map { CardStockItem(card = card) }
        return cardStockItemRepository.saveAll(stockItems)
    }




    fun getOrderById(orderId: Long): Optional<Order> {
        return orderRepository.findById(orderId)
    }

    fun searchOrders(status: OrderStatus, userId: Long, search: String, pageable: Pageable): Page<Order> {
        return orderRepository.findAllByFilter(
            search = search,
            pageable = pageable,
            status = status,
            userId = userId,
        )
    }


}


data class StockItemsCreationData(
    val count: Int,
    val cardId: Long
)

data class CardCreationData(
    val title: String,
    val description: String,
    val price: Int,
    val code: String,
    val imageUrl: String,
    val isActive: Boolean,
    val pricingType: PricingType,
    val deliveryType: DeliveryType
)