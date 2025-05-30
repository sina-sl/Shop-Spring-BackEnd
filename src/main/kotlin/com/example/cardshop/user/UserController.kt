package com.example.cardshop.user

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/me")
class UserController(
    private val userService: UserService
) {
    @GetMapping
    fun getProfile(@AuthenticationPrincipal principal: CustomUserDetails): ResponseEntity<User> {
        return ResponseEntity.ok(principal.user)
    }

//    @GetMapping("/cards")
//    fun getUserCards(@AuthenticationPrincipal principal: CustomUserDetails): ResponseEntity<List<PurchasedCard>> {
//        val cards = userService.getPurchasedCards(principal.id)
//        return ResponseEntity.ok(cards)
//    }
}