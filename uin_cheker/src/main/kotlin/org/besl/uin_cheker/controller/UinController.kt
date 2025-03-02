package org.besl.uin_cheker.controller

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.besl.uin_cheker.entity.RequestUinHistory
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
        return try {
            val status = probPalataClient.getAsyncStatus(uin, "REST")
            val objectMapper = jacksonObjectMapper().apply {
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
            }
            val rs = objectMapper.writeValueAsString(status)
            ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(rs)
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("Error: ${e.message}")
        }
    }
}