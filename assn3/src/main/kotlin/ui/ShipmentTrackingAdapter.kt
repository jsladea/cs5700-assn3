package ui

import models.domainmodels.ShipmentDomainModel
import models.interfaces.IObserver
import models.viewmodels.ShipmentViewModel
import server.repositories.ShipmentRepository

class ShipmentTrackingAdapter(
    private val onShipmentUpdate: (String, ShipmentViewModel) -> Unit
) : IObserver<ShipmentDomainModel> {

    private val trackedIds = mutableSetOf<String>()

    fun trackShipment(id: String) {
        val shipment = ShipmentRepository.getShipment(id);
        if (trackedIds.contains(id)) throw IllegalStateException("Shipment is already being tracked")
        if (shipment == null) throw NoSuchElementException("Shipment with ID $id does not exist")
        shipment.addObserver(this)
        trackedIds.add(id)
        onShipmentUpdate(id, shipment.toViewModel())
    }

    fun removeShipmentTracking(id: String) {
        val shipment = ShipmentRepository.getShipment(id);
        if (!trackedIds.contains(id) || shipment == null) return
        shipment.removeObserver(this)
        trackedIds.remove(id)
    }

    override fun update(observable: ShipmentDomainModel) {
        onShipmentUpdate(observable.id, observable.toViewModel())
    }
}