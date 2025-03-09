package org.besl.uin_cheker.controller.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ServiceController {
    @GetMapping("/health")
    fun healthHandler(): String {
        return "ok"
    }
}