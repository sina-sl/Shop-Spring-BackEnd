package com.example.cardshop.user

import com.example.cardshop.order.Order
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true, nullable = false)
    val email: String,
    @Column(nullable = false)
    val passwordHash: String,
    @Enumerated(EnumType.STRING)
    val role: UserRole = UserRole.USER,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val provider: String = "local",

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    val orders: List<Order> = emptyList(),

//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    @JsonIgnore
//    val purchasedCards: List<PurchasedCard> = emptyList()
) {

    @JsonIgnore
    fun toUserDetails(): CustomUserDetails {
        return CustomUserDetails(this)
    }

}


data class CustomUserDetails( val user: User ) : UserDetails {

    val id = user.id
    override fun isEnabled(): Boolean = true
    override fun getUsername(): String = user.id.toString()
    override fun getPassword(): String = user.passwordHash

    override fun isAccountNonLocked(): Boolean = true
    override fun isAccountNonExpired(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun getAuthorities(): List<SimpleGrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_${user.role}"))

}