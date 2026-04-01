# ─── Build stage ─────────────────────────────────────────────
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY src ./src

RUN ./gradlew build --no-daemon -x test

# ─── Runtime stage ───────────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

# Копируем собранный JAR и драйвер
COPY --from=builder /app/build/libs/*.jar app.jar
RUN wget -q https://jdbc.postgresql.org/download/postgresql-42.7.3.jar -O postgresql.jar

# Исправь package на свой!
CMD ["java", "-cp", "app.jar:postgresql.jar", "ru.hexaend.Main"]