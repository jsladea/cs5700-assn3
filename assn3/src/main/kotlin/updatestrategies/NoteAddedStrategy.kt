package updatestrategies

import models.datamodels.ShippingUpdateDataModel
import models.domainmodels.ShipmentDomainModel

class NoteAddedStrategy : IShipmentUpdateStrategy {
    override fun apply(shipment: ShipmentDomainModel, update: ShippingUpdateDataModel) {
        update.otherInfo?.let { shipment.addNote(it) }
    }
}