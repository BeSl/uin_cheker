package org.besl.uin_cheker.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.ErrorResponseException
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime

@ExceptionHandler(IllegalArgumentException::class)
fun handleValidationExceptions(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
    val response = ErrorResponseException(HttpStatus.BAD_REQUEST, ex)
    return ResponseEntity(response, HttpStatus.BAD_REQUEST)
}