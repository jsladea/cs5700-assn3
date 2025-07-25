package server.repositories

import models.domainmodels.ShipmentDomainModel

object ShipmentRepository {
    private val shipments = mutableMapOf<String, ShipmentDomainModel>()

    fun getShipment(id: String): ShipmentDomainModel? = shipments[id]

    fun upsertShipment(shipment: ShipmentDomainModel) {
        shipments[shipment.id] = shipment
    }
}