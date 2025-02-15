package org.besl.uin_cheker.service

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
    val baseUrl = """https://probpalata.gov.ru"""
    val url_captcha = """/captcha/"""
    val url_check = """/check-uin/"""
    private var cookies: String? = null

    fun getAsyncStatus(uin: String): String{

        val bUrl = """https://probpalata.gov.ru"""
        val client = WebClient.create("$baseUrl")
        val resp = client.get()
        val imgData = client
            .post()
            .uri("$url_captcha")
            .accept(MediaType.IMAGE_PNG)
            .exchangeToMono { resp ->
                resp.headers().asHttpHeaders().getFirst("Set-Cookie")
                    ?.let { cookies = it }
                resp.bodyToMono<ByteArray>()
            }
            .block()


        val baseFilePath = generateUniqueString()
        val fileName = "$storagePath\\$baseFilePath.png"

        val imgFile = File(fileName)

        imgFile.writeBytes(imgData ?: throw IOException("Empty response"))

        val captchaVal = CaptchaService("$baseFilePath").CaptchaFromFile(imgFile)
        val formData: MultiValueMap<String, String> = LinkedMultiValueMap<String, String>().apply {
            add("action" , "check")
            add("uin", uin)
            add("code" , captchaVal)
        }

        val rr =  client.post()
            .uri("$url_check")
            .header(HttpHeaders.COOKIE, cookies)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono<String>()
            .block()

        return rr.toString()
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