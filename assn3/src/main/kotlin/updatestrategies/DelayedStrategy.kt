package updatestrategies

import constants.ShipmentStatus
import models.datamodels.ShippingUpdateDataModel
import models.domainmodels.ShipmentDomainModel
import models.domainmodels.ShippingUpdateDomainModel
import java.time.Instant

class DelayedStrategy : IShipmentUpdateStrategy {
    override fun apply(shipment: ShipmentDomainModel, update: ShippingUpdateDataModel) {
        val newExpectedDelivery = update.otherInfo?.toLongOrNull()?.let { Instant.ofEpochMilli(it) }
        val shippingUpdate = ShippingUpdateDomainModel(shipment.status, ShipmentStatus.DELAYED, update.updateTimestamp)
        shipment.addUpdate(shippingUpdate)
        shipment.expectedDelivery = newExpectedDelivery
        shipment.status = ShipmentStatus.DELAYED
    }
}