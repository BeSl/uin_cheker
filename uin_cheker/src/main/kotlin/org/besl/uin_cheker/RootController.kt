package org.besl.uin_cheker

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class RootController {
    @GetMapping("/health")
    fun HealthHandler(): String {
        return "ok"
    }
}