package org.besl.uin_cheker.controller

import org.besl.uin_cheker.model.*
import org.besl.uin_cheker.service.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/")
class CaptchaController (
    private val captchaService: CaptchaService
){
    @PostMapping("/solve")
    fun solveCaptcha(
        @RequestBody request: CaptchaRequest
    ): String {
        val res = captchaService.processCaptcha(request.imageBase64)
        return res
    }
}