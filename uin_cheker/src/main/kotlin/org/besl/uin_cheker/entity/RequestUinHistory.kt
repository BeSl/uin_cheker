package org.besl.uin_cheker.entity

import jakarta.persistence.*
import org.besl.uin_cheker.dto.response.HistoryDto
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import kotlin.time.Duration

@Entity
//@EnableJpaRepositories
@Table(name = "request_history")
class RequestUinHistory (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 16)
    val uin: String,

    @Column(nullable = false)
    @CreationTimestamp
    val requestDate: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: RequestStatus = RequestStatus.PENDING,

    @Column(columnDefinition = "TEXT")
    var responseData: String? = null,

    @Column(length = 50)
    var source: String = "WEB", // WEB или API
    @Column(nullable = false, length = 16)
    val requestData: String="",
    @Column(nullable = false, length = 16)
    val timestamp: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false, length = 16)
    val failedChecked: Boolean = false,
    @Column(nullable = false, length = 16)
    val duration: Duration = Duration.ZERO,
    @Column(nullable = true, length = 16)
    var captchaPath: String? = null,
    @Column(nullable = true, length = 16)
    var captchaText: String? = null

) {
    constructor() : this(uin="") {

    }

    enum class RequestStatus { SUCCESS, ERROR, PENDING }

    fun toDto() = HistoryDto(
        uin = this.uin,
        timestamp = this.requestDate,
        status = this.status.toString(),
        typeClient = this.source,
    )
}