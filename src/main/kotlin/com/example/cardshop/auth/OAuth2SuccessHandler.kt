package com.example.cardshop.auth

import com.example.cardshop.config.JwtUtil
import com.example.cardshop.user.User
import com.example.cardshop.user.UserRepository
import com.example.cardshop.user.UserRole
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2SuccessHandler(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        val oAuth2User = authentication?.principal as? OAuth2User ?: return
        val email = oAuth2User.getAttribute<String>("email") ?: return
        val user = userRepository.findByEmail(email).orElse(
            userRepository.save(
                User(
                    email = email,
                    passwordHash = "",
                    role = UserRole.USER,
                    provider = "google"
                )
            )
        )

        val token = jwtUtil.generateToken(user)
        // Redirect to frontend with token as query param
        val redirectUrl = "http://localhost:3000/oauth2/redirect?token=$token"
        response?.sendRedirect(redirectUrl)
    }
} 