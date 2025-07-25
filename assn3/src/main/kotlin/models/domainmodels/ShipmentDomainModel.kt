package models.domainmodels

import constants.ShipmentStatus
import constants.UpdateType
import models.datamodels.ShippingUpdateDataModel
import models.interfaces.IObservable
import models.interfaces.IObserver
import models.viewmodels.ShipmentViewModel
import updatestrategies.IShipmentUpdateStrategy
import java.time.Instant
import java.time.temporal.ChronoUnit

sealed class ShipmentDomainModel(
    val id: String,
    protected val strategies: Map<UpdateType, IShipmentUpdateStrategy>,
    val creationTime: Instant,
) : IObservable<ShipmentDomainModel> {
    private val _notes = mutableListOf<String>()
    private val _updates = mutableListOf<ShippingUpdateDomainModel>()
    private val observers = mutableListOf<IObserver<ShipmentDomainModel>>()
    protected val abnormalEvents = mutableListOf<String>()
    protected abstract val shipmentType: String

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

    abstract fun validateUpdate(update: ShippingUpdateDataModel)

    fun update(update: ShippingUpdateDataModel) {
        validateUpdate(update)
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

    fun addAbnormalEvent(event: String) {
        abnormalEvents.add(event)
    }

    fun toViewModel(): ShipmentViewModel {
        return ShipmentViewModel(
            id = this.id,
            status = this.status,
            currentLocation = this.currentLocation,
            expectedDelivery = this.expectedDelivery,
            notes = this.notes,
            updates = this.updates,
            abnormalEvents = abnormalEvents.toList(),
            shipmentType = shipmentType
        )
    }

    private fun notifyObservers() {
        observers.forEach { it.update(this)}
    }
}

class StandardShipment(
    id: String,
    strategies: Map<UpdateType, IShipmentUpdateStrategy>,
    creationTime: Instant,
) : ShipmentDomainModel(id, strategies, creationTime) {
    override val shipmentType: String
        get() = "standard"

    override fun validateUpdate(update: ShippingUpdateDataModel) {
        // No special rules for StandardShipment
    }
}

class ExpressShipment(
    id: String,
    strategies: Map<UpdateType, IShipmentUpdateStrategy>,
    creationTime: Instant,
) : ShipmentDomainModel(id, strategies, creationTime) {

    override val shipmentType: String
        get() = "express"

    override fun validateUpdate(update: ShippingUpdateDataModel) {
        val newExpectedDelivery = update.otherInfo?.toLongOrNull()?.let { Instant.ofEpochMilli(it) }
        if (newExpectedDelivery != null && newExpectedDelivery.isAfter(creationTime.plus(3, ChronoUnit.DAYS))) {
            addAbnormalEvent("Express shipment delivery date is more than 3 days after creation.")
        }
    }
}

class OvernightShipment(
    id: String,
    strategies: Map<UpdateType, IShipmentUpdateStrategy>,
    creationTime: Instant,
) : ShipmentDomainModel(id, strategies, creationTime) {

    override val shipmentType: String
        get() = "overnight"

    override fun validateUpdate(update: ShippingUpdateDataModel) {
        val newExpectedDelivery = update.otherInfo?.toLongOrNull()?.let { Instant.ofEpochMilli(it) }
        if (newExpectedDelivery != null && newExpectedDelivery.isAfter(creationTime.plus(1, ChronoUnit.DAYS))) {
            // Only abnormal if not a DELAYED update
            if (update.type != UpdateType.DELAYED) {
                addAbnormalEvent("Overnight shipment delivery date is more than 24 hours after creation.")
            }
        }
    }
}

class BulkShipment(
    id: String,
    strategies: Map<UpdateType, IShipmentUpdateStrategy>,
    creationTime: Instant,
) : ShipmentDomainModel(id, strategies, creationTime) {

    override val shipmentType: String
        get() = "bulk"

    override fun validateUpdate(update: ShippingUpdateDataModel) {
        val newExpectedDelivery = update.otherInfo?.toLongOrNull()?.let { Instant.ofEpochMilli(it) }
        if (newExpectedDelivery != null && newExpectedDelivery.isBefore(creationTime.plus(3, ChronoUnit.DAYS))) {
            addAbnormalEvent("Bulk shipment delivery date is sooner than 3 days after creation.")
        }
    }
}

