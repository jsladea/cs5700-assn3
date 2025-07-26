package server.controllers

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.viewmodels.ShippingUpdateViewModel
import server.services.ShipmentService

object ShipmentController {
    fun register(route: Route) {
        route.post("/shipment/create") {
            try {
                val viewModel = call.receive<ShippingUpdateViewModel>()
                val shipment = ShipmentService.createShipment(viewModel)
                call.respond(shipment)
            } catch (e: IllegalArgumentException) {
                if (e.message?.contains("already exists") == true) {
                    call.respondText("Shipment already exists", status = io.ktor.http.HttpStatusCode.Conflict)
                } else {
                    call.respondText("Internal server error", status = io.ktor.http.HttpStatusCode.InternalServerError)
                }
            } catch (e: Exception) {
                call.respondText("Internal server error", status = io.ktor.http.HttpStatusCode.InternalServerError)
            }
        }

        route.patch("/shipment/update") {
            try {
                val viewModel = call.receive<ShippingUpdateViewModel>()
                val shipment = ShipmentService.updateShipment(viewModel)
                if (shipment == null) {
                    call.respondText("Shipment not found", status = io.ktor.http.HttpStatusCode.NotFound)
                } else {
                    call.respond(shipment)
                }
            } catch (e: IllegalArgumentException) {
                call.respondText("Internal server error", status = io.ktor.http.HttpStatusCode.InternalServerError)
            } catch (e: Exception) {
                call.respondText("Internal server error", status = io.ktor.http.HttpStatusCode.InternalServerError)
            }
        }

        route.get("/shipment/{id}") {
            val id = call.parameters["id"]!!
            val shipment = ShipmentService.getShipment(id)
            if (shipment == null) {
                call.respondText("Shipment not found", status = io.ktor.http.HttpStatusCode.NotFound)
            } else {
                call.respond(shipment)
            }
        }
    }
}