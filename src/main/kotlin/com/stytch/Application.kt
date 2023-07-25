package com.stytch

import com.stytch.java.consumer.StytchClient
import com.stytch.plugins.configureRouting
import freemarker.cache.ClassTemplateLoader
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.freemarker.FreeMarker
import io.ktor.server.netty.Netty

const val HOST = "localhost"
const val PORT = 3000

fun main() {
    val dotenv = Dotenv.configure().filename("local.properties").load()
    StytchClient.configure(
        projectId = dotenv.get("STYTCH_PROJECT_ID"),
        secret = dotenv.get("STYTCH_PROJECT_SECRET"),
    )
    embeddedServer(
        factory = Netty,
        port = PORT,
        host = HOST,
        module = Application::module,
    ).start(wait = true)
}

fun Application.module() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    configureRouting()
}
