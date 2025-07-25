package models.domainmodels

import constants.ShipmentStatus
import constants.UpdateType
import models.datamodels.ShippingUpdateDataModel
import models.interfaces.IObservable
import models.interfaces.IObserver
import models.viewmodels.ShipmentViewModel
import updatestrategies.IShipmentUpdateStrategy
import java.time.Instant

class ShipmentDomainModel(val id: String, private val strategies: Map<UpdateType, IShipmentUpdateStrategy>) : IObservable<ShipmentDomainModel> {

    private val _notes = mutableListOf<String>()
    private val _updates = mutableListOf<ShippingUpdateDomainModel>()
    private val observers = mutableListOf<IObserver<ShipmentDomainModel>>()

    var status: ShipmentStatus = ShipmentStatus.CREATED
    var currentLocation: String = ""
    var expectedDelivery: Instant? = null
    val notes: List<String>
        get() = _notes.toList()
    val updates: List<ShippingUpdateDomainModel>
        get() = _updates.toList()



    override fun addObserver(observer: IObserver<ShipmentDomainModel>) {
        observers += observer
    }

    override fun removeObserver(observer: IObserver<ShipmentDomainModel>) {
        observers -= observer
    }

    fun update(update: ShippingUpdateDataModel) {
        strategies[update.type]?.apply(this, update)
            ?: throw IllegalArgumentException("Unknown update type: ${update.type}")
        notifyObservers()
    }

    fun addUpdate(update: ShippingUpdateDomainModel) {
        _updates.add(update)
    }

    fun addNote(note: String) {
        _notes.add(note)
    }

    fun toViewModel(): ShipmentViewModel {
        return ShipmentViewModel(
            id = this.id,
            status = this.status,
            currentLocation = this.currentLocation,
            expectedDelivery = this.expectedDelivery,
            notes = this.notes,
            updates = this.updates
        )
    }

    private fun notifyObservers() {
        observers.forEach { it.update(this)}
    }

}