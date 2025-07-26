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
            val viewModel = call.receive<ShippingUpdateViewModel>()
            try {
                val shipment = ShipmentService.createShipment(viewModel)
                call.respond(shipment)
            } catch (e: Exception) {
                call.respondText("Shipment already exists", status = io.ktor.http.HttpStatusCode.Conflict)
            }
        }

        route.patch("/shipment/update") {
            val viewModel = call.receive<ShippingUpdateViewModel>()
            val shipment = ShipmentService.updateShipment(viewModel)
            if (shipment == null) {
                call.respondText("Shipment not found", status = io.ktor.http.HttpStatusCode.NotFound)
            } else {
                call.respond(shipment)
            }
        }

        route.get("/shipment/{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText("Missing id", status = io.ktor.http.HttpStatusCode.BadRequest)
            val shipment = ShipmentService.getShipment(id)
            if (shipment == null) {
                call.respondText("Shipment not found", status = io.ktor.http.HttpStatusCode.NotFound)
            } else {
                call.respond(shipment)
            }
        }
    }
}