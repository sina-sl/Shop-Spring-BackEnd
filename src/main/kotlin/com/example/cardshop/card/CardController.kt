package com.example.cardshop.card

import com.example.cardshop.card.dto.CardWithStockCountDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cards")
class CardController(
    private val cardService: CardService,
) {

    @GetMapping
    fun searchActiveCards(
        @RequestParam(required = false) search: String?,
        pageable: Pageable
    ): Page<CardWithStockCountDto> = cardService.searchActiveCards(search ?: "", pageable)


    @GetMapping("/{id}")
    fun getActiveCard(@PathVariable id: Long): ResponseEntity<Card> {
        val card = cardService.getActiveCardById(id)
        return if (card.isPresent) {
            ResponseEntity.ok(card.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
} 