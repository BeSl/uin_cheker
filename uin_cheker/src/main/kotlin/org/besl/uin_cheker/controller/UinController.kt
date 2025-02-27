package org.besl.uin_cheker.controller

import org.besl.uin_cheker.model.RequestUinHistory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.besl.uin_cheker.service.*
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/uin")
class UinController (
    private val probPalataClient: ProbPalataService
){

    @GetMapping("/{uin}")
    fun InfoUinStatus(
        @PathVariable uin: String
    ): ResponseEntity<String> {
        val history = RequestUinHistory(requestData = uin,)
        return try {
            val status = probPalataClient.getAsyncStatus(uin, history)
            ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(status)
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("Error: ${e.message}")
        }
    }
}