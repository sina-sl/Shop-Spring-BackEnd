package com.example.cardshop.config

import com.example.cardshop.user.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import kotlin.jvm.optionals.getOrNull

@Component
class JwtAuthFilter(
    private val jwtUtil: JwtUtil, // Placeholder for JWT utility
    private val userService: UserService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Only try to get token from cookie
        val jwt = request.cookies?.firstOrNull { it.name == "jwt" }?.value
        val claims = jwt?.let { jwtUtil.getClaims(it) }
        val userId = claims?.getUserId()

        if (userId != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userService.getUserProfile(userId).getOrNull()?.toUserDetails()
            if (userDetails != null && !claims.isExpired()) {
                val authentication = jwtUtil.getAuthentication(userDetails)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        filterChain.doFilter(request, response)
    }
} 