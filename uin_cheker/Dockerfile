# Сборка приложения
FROM eclipse-temurin:21-jdk-jammy as builder

# Установка системных зависимостей для Tesseract и OpenCV
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-rus \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .
RUN ./gradlew bootJar

# Финальный образ
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Копирование собранного JAR-файла
COPY --from=builder /app/build/libs/*.jar app.jar

# Установка runtime зависимостей
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-rus \
    && rm -rf /var/lib/apt/lists/*

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]