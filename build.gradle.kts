plugins {
    id("java")
    id("war")
}

group = "ru.hexaend"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

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