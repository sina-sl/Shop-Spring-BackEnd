package com.example.cardshop.user

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
): UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        // in userDetails, username is userId
        return username?.toLong()?.let {
            userRepository.findById(it).orElseThrow {
                RuntimeException("User not found")
            }.toUserDetails()
        } ?: throw RuntimeException("User not found")
    }

    fun getUserByEmail(email: String): Optional<User> {
        return userRepository.findByEmail(email)
    }

    fun getUserProfile(id: Long): Optional<User> {
        return userRepository.findById(id)
    }

//    fun getPurchasedCards(id: Long): List<PurchasedCard> {
//        val user = getUserProfile(id).orElseThrow { RuntimeException("User Not Found") }
//        return purchasedCardRepository.findByUser(user)
//    }
}