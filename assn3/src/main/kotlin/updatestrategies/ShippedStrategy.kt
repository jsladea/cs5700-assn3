package updatestrategies

import constants.ShipmentStatus
import models.datamodels.ShippingUpdateDataModel
import models.domainmodels.ShipmentDomainModel
import models.domainmodels.ShippingUpdateDomainModel
import java.time.Instant

class ShippedStrategy : IShipmentUpdateStrategy {
    override fun apply(shipment: ShipmentDomainModel, update: ShippingUpdateDataModel) {
        val expectedDelivery = update.otherInfo?.toLongOrNull()?.let { Instant.ofEpochMilli(it) }
        val shippingUpdate = ShippingUpdateDomainModel(shipment.status, ShipmentStatus.SHIPPED, update.updateTimestamp)
        shipment.addUpdate(shippingUpdate)
        shipment.expectedDelivery = expectedDelivery
        shipment.status = ShipmentStatus.SHIPPED
    }
}