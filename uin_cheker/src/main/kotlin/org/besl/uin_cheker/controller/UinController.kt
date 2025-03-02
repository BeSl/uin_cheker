package org.besl.uin_cheker.controller

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.besl.uin_cheker.entity.RequestUinHistory
import org.besl.uin_cheker.model.HistoryDto
import org.besl.uin_cheker.repository.RequestHistoryRepository
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.besl.uin_cheker.service.*
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/uin")
class UinController (
    private val probPalataClient: ProbPalataService,
    private val historyService: HistoryService
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

    @GetMapping("/rhistory")
    fun getHistory(
        @RequestParam(required = false) uin: String?,
        @RequestParam(required = false) typeClient: String?,
    ): ResponseEntity<List<HistoryDto>> {


        return ResponseEntity.ok(
                    historyService.getHistory(uin, typeClient)
        )
    }
}