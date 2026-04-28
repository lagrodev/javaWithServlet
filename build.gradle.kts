plugins {
    id("java")
    id("war")
    id("com.diffplug.spotless") version "6.25.0"
}

group = "ru.hexaend"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.testcontainers:testcontainers:1.20.4")
    testImplementation("org.testcontainers:postgresql:1.20.4")
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")

    // Embedded Tomcat (для HTTP-интеграционных тестов)
    testImplementation("org.apache.tomcat.embed:tomcat-embed-core:10.1.30")
    testImplementation("org.apache.tomcat.embed:tomcat-embed-jasper:10.1.30")

    implementation("org.postgresql:postgresql:42.7.3")
    // сервлет
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")

    // JSTL
    implementation("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:3.0.0")
    runtimeOnly("org.glassfish.web:jakarta.servlet.jsp.jstl:3.0.1")
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    java {
        googleJavaFormat("1.22.0")
        target("src/main/java/**/*.java", "src/test/java/**/*.java")
    }
}