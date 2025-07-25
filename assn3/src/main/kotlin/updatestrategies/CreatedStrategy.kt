// ============================
// FILE: strategies/CreatedStrategy.kt
// ============================
package updatestrategies

import constants.ShipmentStatus
import models.datamodels.ShippingUpdateDataModel
import models.domainmodels.ShipmentDomainModel
import models.domainmodels.ShippingUpdateDomainModel


class CreatedStrategy : IShipmentUpdateStrategy {
    override fun apply(shipment: ShipmentDomainModel, update: ShippingUpdateDataModel) {
        val shippingUpdate = ShippingUpdateDomainModel(
            previousStatus = shipment.status,
            newStatus = ShipmentStatus.CREATED,
            timestamp = update.updateTimestamp
        )
        shipment.addUpdate(shippingUpdate)
        shipment.status = ShipmentStatus.CREATED
    }
}
