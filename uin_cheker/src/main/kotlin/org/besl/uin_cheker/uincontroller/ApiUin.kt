package org.besl.uin_cheker.uincontroller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.besl.uin_cheker.model.*
import org.besl.uin_cheker.service.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/uin")
class ApiUin (
    private val probPalataClient: ProbPalataService
){

    @GetMapping("/{uin}")
    fun InfoUinStatus(
        @PathVariable uin: String
    ): ResponseEntity<String> {
        return try {
            val status = probPalataClient.getAsyncStatus(uin)
//            logger.info("Successfully retrieved status for UIN: $uin")
            ResponseEntity.ok(status)
        } catch (e: Exception) {
//            logger.error("Error processing request for UIN: $uin", e)
            ResponseEntity.internalServerError().body("Error: ${e.message}")
        }
    }
}