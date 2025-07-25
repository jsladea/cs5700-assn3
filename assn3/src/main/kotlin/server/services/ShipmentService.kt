package server.services

import models.domainmodels.ShipmentDomainModel
import models.viewmodels.ShippingUpdateViewModel
import server.repositories.ShipmentRepository

object ShipmentService {
    fun createShipment(update: ShippingUpdateViewModel): ShipmentDomainModel {
        val shipment = ShipmentFactory.createShipment(update.id, update.toDataModel())
        ShipmentRepository.upsertShipment(shipment)
        return shipment
    }

    fun updateShipment(update: ShippingUpdateViewModel): ShipmentDomainModel? {
        val shipment = ShipmentRepository.getShipment(update.id) ?: return null
        shipment.update(update.toDataModel())
        ShipmentRepository.upsertShipment(shipment)
        return shipment
    }

    fun getShipment(id: String): ShipmentDomainModel? = ShipmentRepository.getShipment(id)
}