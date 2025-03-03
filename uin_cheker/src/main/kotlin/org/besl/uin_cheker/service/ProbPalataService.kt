    package org.besl.uin_cheker.service

    import JewelryCheckResponse
    import com.fasterxml.jackson.databind.DeserializationFeature
    import com.fasterxml.jackson.databind.ObjectMapper
    import com.fasterxml.jackson.databind.SerializationFeature
    import com.fasterxml.jackson.module.kotlin.KotlinModule
    import jakarta.transaction.Transactional
    import org.besl.uin_cheker.entity.RequestUinHistory
    import org.besl.uin_cheker.integration.ProbPalataClient
    import org.besl.uin_cheker.repository.RequestHistoryRepository
    import org.springframework.beans.factory.annotation.Value
    import org.springframework.stereotype.Service
    import java.io.File
    import java.io.IOException
    import java.time.LocalDateTime
    import java.time.format.DateTimeFormatter
    import java.util.*


    @Service
    class ProbPalataService(
        @Value("\${captcha.storage.path}") private val storagePath: String,
        private val requestHistoryRepository: RequestHistoryRepository
    ) {

        // Настроенный ObjectMapper
        private val objectMapper: ObjectMapper = ObjectMapper().apply {
            registerModule(KotlinModule.Builder().build())
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        @Transactional
        fun getAsyncStatus(uin: String, source: String = "WEB"): JewelryCheckResponse{

            val history = requestHistoryRepository.save(
                RequestUinHistory(
                    uin = uin,
                    source = source,
                    status = RequestUinHistory.RequestStatus.PENDING
                )
            )

            try {
                val pClient = ProbPalataClient()
                val imgData = pClient.getImgCaptha()

                val baseFilePath = generateUniqueString()
                val fileName = "$storagePath${File.separator}$baseFilePath.png"
                history.captchaPath = fileName
                File(fileName).writeBytes(imgData ?: throw IOException("Empty response"))

                val captchaVal = CaptchaService("$storagePath${File.separator}$baseFilePath")
                    .CaptchaFromFile(File(fileName))
                history.captchaText = captchaVal
                val res = pClient.uinStatus(uin, captchaVal)

                history.apply {
                    status = RequestUinHistory.RequestStatus.SUCCESS
                    responseData = objectMapper.writeValueAsString(res)
                }.let { requestHistoryRepository.save(it) }

                return res
            } catch (e: Exception) {
                history.apply {
                    status = RequestUinHistory.RequestStatus.ERROR
                    responseData = e.message
                }.let { requestHistoryRepository.save(it) }
                throw e
            }
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