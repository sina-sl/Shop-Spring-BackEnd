package com.example.cardshop.auth

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    data class SignupRequest(val email: String, val password: String)
    data class LoginRequest(val email: String, val password: String)
    data class AuthResponse(val token: String)

    @PostMapping("/signup")
    fun signup(
        @RequestBody req: SignupRequest,
        @RequestHeader("Authorization", required = false) authHeader: String?,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponse> {
        val token = authService.signup(req.email, req.password, authHeader?.removeBearer())
        response.setJwtCookie(token)
        return ResponseEntity.ok(AuthResponse(token))
    }

    @PostMapping("/login")
    fun login(
        @RequestBody req: LoginRequest,
        @RequestHeader("Authorization", required = false) authHeader: String?,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponse> {
        val token = authService.login(req.email, req.password, authHeader?.removeBearer())
        response.setJwtCookie(token)
        return ResponseEntity.ok(AuthResponse(token))
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestHeader("Authorization", required = false) authHeader: String?,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponse> {
        val token = authService.refresh(authHeader)
        response.setJwtCookie(token)
        return ResponseEntity.ok(AuthResponse(token))
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Void> {
        response.setLogoutCookie()
        return ResponseEntity.ok().build()
    }

    private fun String.removeBearer(): String = this.removePrefix("Bearer ").trim()

    private fun HttpServletResponse.setJwtCookie(token: String) {
        this.addHeader("Set-Cookie", "jwt=$token; HttpOnly; Path=/; SameSite=Strict")
    }

    private fun HttpServletResponse.setLogoutCookie() {
        this.addHeader("Set-Cookie", "jwt=; HttpOnly; Path=/; Max-Age=0; SameSite=Strict")
    }
}