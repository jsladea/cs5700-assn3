import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.routing.routing
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.callloging.CallLogging
import server.controllers.ShipmentController
import ui.ShipmentTrackingUI
import io.ktor.server.application.*
import javax.swing.SwingUtilities

import io.ktor.server.plugins.statuspages.StatusPages

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        install(CallLogging)
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                cause.printStackTrace()
                throw cause
            }
        }
        install(CORS) {
            anyHost()
            allowHeader("Content-Type")
            allowMethod(io.ktor.http.HttpMethod.Get)
            allowMethod(io.ktor.http.HttpMethod.Post)
            allowMethod(io.ktor.http.HttpMethod.Patch)
            allowMethod(io.ktor.http.HttpMethod.Options)
        }
        routing {
            ShipmentController.register(this)
        }
    }.start(wait = false)

    SwingUtilities.invokeLater {
        ShipmentTrackingUI()
    }
}