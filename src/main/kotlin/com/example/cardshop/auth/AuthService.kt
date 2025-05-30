package com.example.cardshop.auth

import com.example.cardshop.config.JwtUtil
import com.example.cardshop.exeption.ApiErrorCode
import com.example.cardshop.exeption.GlobalException
import com.example.cardshop.user.User
import com.example.cardshop.user.UserRepository
import com.example.cardshop.user.UserRole
import com.example.cardshop.user.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthService(
    private val jwtUtil: JwtUtil,
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun signup(email: String, password: String, currentToken: String?): String {
        if (currentToken?.let { jwtUtil.getClaims(it)?.getUserId() } != null) {
            throw GlobalException(ApiErrorCode.ALREADY_LOGGED_IN)
        }

        if (userRepository.findByEmail(email).isPresent) {
            throw GlobalException(ApiErrorCode.EMAIL_EXISTS)
        }

        val user = userRepository.save(
            User(
                email = email,
                passwordHash = passwordEncoder.encode(password),
                role = UserRole.USER,
            )
        )

//        val userDetails = userDetailsService.loadUserByUsername(user.email)
        return jwtUtil.generateToken(user)
    }

    fun login(email: String, password: String, currentToken: String?): String {
        if ( currentToken?.let{ jwtUtil.getClaims(currentToken) } != null) {
            throw GlobalException(ApiErrorCode.ALREADY_LOGGED_IN)
        }

        val user = userService.getUserByEmail(email).orElseThrow {
            GlobalException(ApiErrorCode.INVALID_CREDENTIALS)
        }

        if (!passwordEncoder.matches(password, user.passwordHash)) {
            throw GlobalException(ApiErrorCode.INVALID_CREDENTIALS)
        }

        return jwtUtil.generateToken(user)
    }

    fun refresh(token: String?): String {
        val jwt = token?.takeIf { it.startsWith("Bearer ") }?.substring(7)
            ?: throw GlobalException(ApiErrorCode.INVALID_TOKEN)

        val claims = jwtUtil.getClaims(jwt)
        val jwtUserId = claims?.getUserId()
            ?: throw GlobalException(ApiErrorCode.INVALID_TOKEN)

        val realUser = userService.getUserProfile(jwtUserId.toLong()).orElseThrow {
            GlobalException(ApiErrorCode.INVALID_TOKEN)
        }

        if (!claims.isExpired()) {
            throw GlobalException(ApiErrorCode.INVALID_TOKEN)
        }

        return jwtUtil.generateToken(realUser)
    }
}