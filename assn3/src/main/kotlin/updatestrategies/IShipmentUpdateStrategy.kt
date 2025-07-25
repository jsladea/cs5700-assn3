package updatestrategies

import models.datamodels.ShippingUpdateDataModel
import models.domainmodels.ShipmentDomainModel

interface IShipmentUpdateStrategy {
    fun apply(shipment: ShipmentDomainModel, update: ShippingUpdateDataModel)
}