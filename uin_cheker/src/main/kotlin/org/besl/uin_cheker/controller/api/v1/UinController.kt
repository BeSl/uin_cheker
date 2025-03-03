package org.besl.uin_cheker.controller.api.v1

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.besl.uin_cheker.dto.response.HistoryDto
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.besl.uin_cheker.service.*
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/uin")
class UinController (
    private val probPalateClient: ProbPalataService,
    private val historyService: HistoryService
){

    @GetMapping("/{uin}")
    fun infoUinStatus(
        @PathVariable uin: String
    ): ResponseEntity<String> {
        return try {
            val status = probPalateClient.getAsyncStatus(uin, "REST")
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

    @GetMapping("/history")
    fun getHistory(
        @RequestParam(required = false) uin: String?,
        @RequestParam(required = false) typeClient: String?,
    ): ResponseEntity<List<HistoryDto>> {
        return ResponseEntity.ok(
                    historyService.getHistory(uin, typeClient)
        )
    }
}