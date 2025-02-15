package org.besl.uin_cheker.service

import net.sourceforge.tess4j.Tesseract
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.util.*
import org.springframework.web.multipart.MultipartFile

@Service
class CaptchaService(
    @Value("\${captcha.storage.path}") private val storagePath: String
) {

    private val tess = Tesseract().apply {
        setDatapath("""C:\Program Files\Tesseract-OCR\tessdata""")
        setLanguage("rus")
    }

    fun processCaptcha(imageBase64: String): String {
        // Декодируем Base64 в массив байтов
        val imageBytes = Base64.getDecoder().decode(imageBase64)
//        val imageBytes = Base64.getDecoder().decode(imageBase64)
        val contrastImageBytes = replaceGrayWithWhite(imageBytes)
        // Обесцвечиваем картинку
        val blackAndWhiteImageBytes = convertToBlackAndWhite(contrastImageBytes)

        // Сохраняем изображение во временный файл
        val imageFile = File("$storagePath/${UUID.randomUUID()}.png")

        imageFile.writeBytes(imageBytes)
        imageFile.writeBytes(blackAndWhiteImageBytes)

        // Распознаем текст с помощью Tesseract
        return try {
//            println("jj")
//            return ""
            tess.doOCR(imageFile)
        } catch (e: Exception) {
            "ERROR: ${e.message}"
        } finally {
            imageFile.delete() // Удаляем временный файл
        }
    }

    fun CaptchaFromFile(file: File): String {

        val imageBytes = file.readBytes()
        val contrastImageBytes = replaceGrayWithWhite(imageBytes)
        val blackAndWhiteImageBytes = convertToBlackAndWhite(contrastImageBytes)

        val imageFile = File("$storagePath-${UUID.randomUUID()}.png")
        imageFile.writeBytes(imageBytes)

        imageFile.writeBytes(blackAndWhiteImageBytes)
        return try {
            val text = tess.doOCR(imageFile)
            println(text)
            return text
        } catch (e: Exception) {
            "ERROR: ${e.message}"
        } finally {
//            imageFile.delete() // Удаляем временный файл
        }
    }

}