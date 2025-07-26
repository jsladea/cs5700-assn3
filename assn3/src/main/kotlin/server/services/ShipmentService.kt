package server.services

import models.domainmodels.ShipmentDomainModel
import models.viewmodels.ShipmentViewModel
import models.viewmodels.ShippingUpdateViewModel
import server.repositories.ShipmentRepository

object ShipmentService {
    fun createShipment(update: ShippingUpdateViewModel): ShipmentViewModel {
        val existingShipment = ShipmentRepository.getShipment(update.id)
        if (existingShipment != null) {
            throw IllegalArgumentException("Shipment with id \${update.id} already exists")
        }
        val shipment = ShipmentFactory.createShipment(update.id, update.toDataModel())
        ShipmentRepository.upsertShipment(shipment)
        return shipment.toViewModel()
    }

    fun updateShipment(update: ShippingUpdateViewModel): ShipmentViewModel? {
        val shipment = ShipmentRepository.getShipment(update.id) ?: return null
        shipment.update(update.toDataModel())
        ShipmentRepository.upsertShipment(shipment)
        return shipment.toViewModel()
    }

    fun getShipment(id: String): ShipmentViewModel? = ShipmentRepository.getShipment(id)?.toViewModel()
}