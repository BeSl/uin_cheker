package org.besl.uin_cheker.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class OpenCVConfig {
    @PostConstruct
    fun init() {
//        OpenCV.loadLocally()
    }
}