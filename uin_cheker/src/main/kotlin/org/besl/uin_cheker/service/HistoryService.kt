package org.besl.uin_cheker.service

import org.besl.uin_cheker.dto.response.HistoryDto
import org.besl.uin_cheker.repository.RequestHistoryRepository
import org.springframework.stereotype.Service

@Service
class HistoryService(
    private val historyRepository: RequestHistoryRepository
) {
    fun getHistory(
        uin: String?,
        typeClient: String?
    ): List<HistoryDto> {
        return historyRepository.findFiltered(uin,typeClient)
            .map { it.toDto() }
    }
}