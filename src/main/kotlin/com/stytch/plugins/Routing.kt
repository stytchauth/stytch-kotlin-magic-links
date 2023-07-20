package com.stytch.plugins

import com.stytch.HOST
import com.stytch.PORT
import com.stytch.kotlin.common.StytchResult
import com.stytch.kotlin.consumer.StytchClient
import com.stytch.kotlin.consumer.models.magiclinks.AuthenticateRequest
import com.stytch.kotlin.consumer.models.magiclinksemail.LoginOrCreateRequest
import com.stytch.kotlin.consumer.models.sessions.RevokeRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

private const val MAGIC_LINK_URL = "http://$HOST:$PORT/authenticate"

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        staticResources("/static", "static")

        get("/") {
            call.respond(FreeMarkerContent("loginOrSignup.ftl", null))
        }

        post("/login_or_create_user") {
            val formParameters = call.receiveParameters()
            val email = formParameters["email"] ?: ""
            when (val response = StytchClient.magicLinks.email.loginOrCreate(LoginOrCreateRequest(
                email = email,
                loginMagicLinkURL = MAGIC_LINK_URL,
                signupMagicLinkURL = MAGIC_LINK_URL,
            ))) {
                is StytchResult.Success -> call.respond(FreeMarkerContent("emailSent.ftl", null))
                is StytchResult.Error -> throw IllegalStateException(response.exception)
            }
        }

        get("/authenticate") {
            val tokenType = call.request.queryParameters["stytch_token_type"]
                ?: throw IllegalStateException("missing token type")
            if (tokenType != "magic_links") {
                throw IllegalStateException("unsupported token type")
            }
            val token = call.request.queryParameters["token"]
                ?: throw IllegalStateException("missing token")
            when (val response = StytchClient.magicLinks.authenticate(AuthenticateRequest(
                token = token,
                sessionDurationMinutes = 30
            ))) {
                is StytchResult.Success -> call.respond(FreeMarkerContent("loggedIn.ftl", null))
                is StytchResult.Error -> throw IllegalStateException(response.exception)
            }
        }

        get("/logout") {
            // Logging the user out depends on how you choose to persist the session information after authentication
            call.respond(FreeMarkerContent("loggedOut.ftl", null))
        }
    }
}
