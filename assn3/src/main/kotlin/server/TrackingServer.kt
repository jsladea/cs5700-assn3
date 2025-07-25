package server

import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.http.*
import io.ktor.server.routing.routing
import io.ktor.serialization.kotlinx.json.json
import server.controllers.ShipmentController

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { json() }
        install(CORS) { anyHost() }
        routing {
            ShipmentController.register(this)
        }
    }.start(wait = true)
}