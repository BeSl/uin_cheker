package org.besl.uin_cheker.service

import JewelryCheckResponse
import org.besl.uin_cheker.integration.ProbPalataClient
import org.besl.uin_cheker.model.RequestUinHistory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Service
class ProbPalataService(
    @Value("\${captcha.storage.path}") private val storagePath: String
) {




    fun getAsyncStatus(uin: String, history: RequestUinHistory): JewelryCheckResponse{
        val pClient = ProbPalataClient()

        val imgData = pClient.getImgCaptha()

        val baseFilePath = generateUniqueString()
        val fileName = "$storagePath\\$baseFilePath.png"

        val imgFile = File(fileName)

        imgFile.writeBytes(imgData ?: throw IOException("Empty response"))

        history.captchaPath = fileName

        val captchaVal = CaptchaService("$storagePath\\$baseFilePath").CaptchaFromFile(imgFile)

        history.captchaText = captchaVal
        return pClient.uinStatus(uin, captchaVal)
    }

    fun generateUniqueString(): String {
        // Форматирование текущей даты и времени
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        val dateTimeString = currentDateTime.format(formatter)

        // Генерация UUID
        val uuid = UUID.randomUUID().toString().replace("-", "")

        // Объединение компонентов
        return "${dateTimeString}_$uuid"
    }

}

data class ExternalServiceResponse(
    val status: String
)