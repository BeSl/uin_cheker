package org.besl.uin_cheker.dto.response

import java.time.LocalDateTime

class HistoryDto(
    val uin: String?,
    val typeClient: String?,
    val timestamp: LocalDateTime,
    val status:String
)