package org.besl.uin_cheker.model

import java.time.LocalDateTime
import kotlin.time.Duration

//@Entity
class RequestUinHistory(
//    @Id @GeneratedValue
    val id: Long? = null,
    val requestData: String,
    val responseData: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val failedCheked: Boolean = false,
    val duration: Duration = Duration.ZERO,
    var captchaPath: String? = null,
    var captchaText: String? = null
){

}