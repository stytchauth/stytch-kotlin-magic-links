@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm") version "1.9.0"
    id("io.ktor.plugin") version libs.versions.ktor
}

group = "com.stytch"
version = "0.0.1"

application {
    mainClass.set("com.stytch.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.dotenv)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.freemarker)
    implementation(libs.ktor.server.host)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.status)
    implementation(libs.logback)
    implementation(libs.stytch)
}