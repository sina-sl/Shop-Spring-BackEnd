package com.example.cardshop.config

import com.example.cardshop.user.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {
    private val secret: SecretKey = Keys.hmacShaKeyFor("supersecretkeysupersecretkey123456".toByteArray())
    private val expirationMs: Long = 1000 * 60 * 60 // 1 hour

    fun generateToken(user: User): String {
        val claims = mutableMapOf<String, Any>()
        return Jwts.builder()
            .subject(user.id.toString())
            .claims(claims)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expirationMs))
            .signWith(secret, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getAuthentication(userDetails: UserDetails): Authentication {
        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }

    fun getClaims(token: String): JwtClaims? {
        return try {
            Jwts.parser().verifyWith(secret).build().parseSignedClaims(token).payload.let {
                JwtClaims(it)
            }
        } catch (e: Exception) {
            null
        }
    }

}

class JwtClaims(private var claims: Claims) {
    fun isExpired(): Boolean {
        return claims.expiration.before(Date())
    }

    fun getUserId(): Long? {
        return try {
            claims.subject.toLong()
        } catch (e: Exception) {
            null
        }
    }
}