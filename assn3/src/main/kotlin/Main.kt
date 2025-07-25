import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.routing.routing
import io.ktor.serialization.kotlinx.json.json
import server.controllers.ShipmentController
import ui.ShipmentTrackingUI
import io.ktor.server.application.*
import javax.swing.SwingUtilities

fun main() {
    // Start Ktor server in the background (non-blocking)
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { json() }
        install(CORS) { anyHost() }
        routing {
            ShipmentController.register(this)
        }
    }.start(wait = false)

    // Launch the UI
    SwingUtilities.invokeLater {
        ShipmentTrackingUI()
    }
}