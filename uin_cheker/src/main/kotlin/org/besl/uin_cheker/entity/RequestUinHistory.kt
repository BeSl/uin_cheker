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

    @Column()
    var responseData: String? = "",

    @Column(length = 50)
    var source: String = "WEB", // WEB или API
    @Column(nullable = false)
    val requestData: String="",
    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false)
    val failedChecked: Boolean = false,
    @Column(nullable = false)
    val duration: Duration = Duration.ZERO,
    @Column(nullable = true)
    var captchaPath: String? = null,
    @Column(nullable = true)
    var captchaText: String? = null

) {
    constructor() : this(uin="") {

    }

    enum class RequestStatus { SUCCESS, ERROR, PENDING }

    fun toDto() = HistoryDto(
        uin = this.uin,
        typeClient = this.source,
        timestamp = this.requestDate,
        statusRequest = this.status.toString(),
        capthaText = this.captchaText,
        responseData = this.responseData,
    )
}