package com.example.cardshop.exeption

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(GlobalException::class)
    fun handleAlreadyLoggedIn(ex: GlobalException): ResponseEntity<ApiError> =
        ResponseEntity.status(409).body(ex.code.toApiError())
}

class ApiError(
    code: ApiErrorCode
) {
    val error: String = code.name
    val message: String = code.message
    val httpStatus: Int = code.httpStatus.value()
}

class GlobalException(val code: ApiErrorCode) : RuntimeException()

enum class ApiErrorCode(
    val message: String,
    val httpStatus: HttpStatus
) {
    // Conflict when user tries to sign up or log in while already logged in
    ALREADY_LOGGED_IN(
        message = "User is already logged in.",
        httpStatus = HttpStatus.CONFLICT
    ),

    // Conflict when the email already exists in the database
    EMAIL_EXISTS(
        message = "Email is already in use.",
        httpStatus = HttpStatus.CONFLICT
    ),

    // Unauthorized when user credentials are invalid during login
    INVALID_CREDENTIALS(
        message = "Invalid email or password.",
        httpStatus = HttpStatus.UNAUTHORIZED
    ),

    // Unauthorized when token is missing, expired or invalid
    INVALID_TOKEN(
        message = "Authentication token is invalid or expired.",
        httpStatus = HttpStatus.UNAUTHORIZED
    );


    fun toApiError() : ApiError {
        return ApiError(this)
    }
}

