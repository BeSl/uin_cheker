package org.besl.uin_cheker.integration

import JewelryCheckResponse
import org.besl.uin_cheker.main
import org.besl.uin_cheker.service.JewelryHtmlParser
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.BodyInserters

//@Suppress("NAME_SHADOWING")
class ProbPalataClient {
    val baseUrl = """https://probpalata.gov.ru"""
    val url_captcha = """/captcha/"""
    val url_check = """/check-uin/"""

    private val client = WebClient.create(baseUrl)
    private var cookies: String? = null

    fun getImgCaptha(): ByteArray?{

        val resp = client.get()
        return client
            .post()
            .uri(url_captcha)
            .accept(MediaType.IMAGE_PNG)
            .exchangeToMono { resp ->
                resp.headers().asHttpHeaders().getFirst("Set-Cookie")
                    ?.let { cookies = it }
                resp.bodyToMono<ByteArray>()
            }
            .block()
    }

    fun uinStatus(uin: String, captcha: String): JewelryCheckResponse {
        val formData: MultiValueMap<String, String> = LinkedMultiValueMap<String, String>().apply {
            add("action" , "check")
            add("uin", uin)
            add("code" , captcha)
        }

        val rr =  client.post()
            .uri(url_check)
            .header(HttpHeaders.COOKIE, cookies)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono<String>()
            .block()

        val parser = JewelryHtmlParser()
        val response = parser.parse(rr.toString())

        return  response

    }


}