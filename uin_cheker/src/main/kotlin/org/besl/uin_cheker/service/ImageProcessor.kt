package org.besl.uin_cheker.service

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.io.OutputStream as IoOutputStream

import java.awt.image.DataBufferByte
fun convertToBlackAndWhite(imageBytes: ByteArray): ByteArray {
    // Преобразуем массив байтов в BufferedImage
    val inputStream = ByteArrayInputStream(imageBytes)
    val bufferedImage = ImageIO.read(inputStream)

    // Создаем новое черно-белое изображение
    val blackAndWhiteImage = BufferedImage(
        bufferedImage.width,
        bufferedImage.height,
        BufferedImage.TYPE_BYTE_GRAY
    )

    // Рисуем исходное изображение на черно-белом
    val graphics = blackAndWhiteImage.createGraphics()
    graphics.drawImage(bufferedImage, 0, 0, null)
    graphics.dispose()

    // Преобразуем BufferedImage обратно в массив байтов
    val outputStream = ByteArrayOutputStream()
    ImageIO.write(blackAndWhiteImage, "png", outputStream)
    return outputStream.toByteArray()
}

fun increaseContrastWithOpenCV(imageBytes: ByteArray): ByteArray {
    // Загружаем библиотеку OpenCV
    System.setProperty("java.library.path", """C:\opencv\build\java\x64\""")
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

    println("OpenCV version: ${Core.VERSION}")
    println(System.getProperty("java.library.path"))
//    val gray = Mat()

//     Преобразуем массив байтов в BufferedImage
    val bufferedImage = ImageIO.read(ByteArrayInputStream(imageBytes))

    // Преобразуем BufferedImage в массив байтов в формате RGB
    val data = (bufferedImage.raster.dataBuffer as DataBufferByte).data

    // Создаем Mat из данных BufferedImage
    val mat = Mat(bufferedImage.height, bufferedImage.width, CvType.CV_8UC3)
    mat.put(0, 0, data)

    // Преобразуем изображение в черно-белое
    val grayMat = Mat()
    Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY)

    // Применяем гистограммное выравнивание
    val equalizedMat = Mat()
    Imgproc.equalizeHist(grayMat, equalizedMat)

    // Преобразуем Mat обратно в BufferedImage
    val outputImage = BufferedImage(
        equalizedMat.cols(),
        equalizedMat.rows(),
        BufferedImage.TYPE_BYTE_GRAY
    )
    val outputData = (outputImage.raster.dataBuffer as DataBufferByte).data
    equalizedMat.get(0, 0, outputData)

    // Преобразуем BufferedImage в массив байтов
    val outputStream = ByteArrayOutputStream()
    ImageIO.write(outputImage, "png", outputStream)
    return outputStream.toByteArray()
}

fun replaceGrayWithWhite(imageBytes: ByteArray): ByteArray {
    // Загрузка нативных библиотек OpenCV
    System.setProperty("java.library.path", """C:\opencv\build\java\x64\""")
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)


    // Преобразуем массив байтов в BufferedImage
    val bufferedImage = ImageIO.read(ByteArrayInputStream(imageBytes))

    // Преобразуем BufferedImage в Mat
    val mat = Mat(bufferedImage.height, bufferedImage.width, CvType.CV_8UC3)
    val data = (bufferedImage.raster.dataBuffer as DataBufferByte).data
    mat.put(0, 0, data)

    // Преобразуем изображение в черно-белое
    val grayMat = Mat()
    Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY)

    // Определяем диапазон серого цвета (например, от 100 до 200)
    val lowerGray = Scalar(100.0)
    val upperGray = Scalar(200.0)

    // Создаем маску для серых пикселей
    val mask = Mat()
    Core.inRange(grayMat, lowerGray, upperGray, mask)

    // Заменяем серые пиксели на белые
    val whiteMat = Mat(grayMat.size(), grayMat.type(), Scalar(255.0))
    Core.copyTo(whiteMat, grayMat, mask)

    // Преобразуем Mat обратно в BufferedImage
    val outputImage = BufferedImage(
        grayMat.cols(),
        grayMat.rows(),
        BufferedImage.TYPE_BYTE_GRAY
    )
    val outputData = (outputImage.raster.dataBuffer as DataBufferByte).data
    grayMat.get(0, 0, outputData)

    // Преобразуем BufferedImage в массив байтов
    val outputStream = ByteArrayOutputStream()
    ImageIO.write(outputImage, "png", outputStream)
    return outputStream.toByteArray()
}