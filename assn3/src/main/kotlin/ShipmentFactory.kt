import constants.UpdateType
import models.datamodels.ShippingUpdateDataModel
import models.domainmodels.BulkShipment
import models.domainmodels.ExpressShipment
import models.domainmodels.OvernightShipment
import models.domainmodels.ShipmentDomainModel
import models.domainmodels.StandardShipment
import updatestrategies.CanceledStrategy
import updatestrategies.CreatedStrategy
import updatestrategies.DelayedStrategy
import updatestrategies.DeliveredStrategy
import updatestrategies.IShipmentUpdateStrategy
import updatestrategies.LocationStrategy
import updatestrategies.LostStrategy
import updatestrategies.NoteAddedStrategy
import updatestrategies.ShippedStrategy

object ShipmentFactory {

    private val strategyMap = mapOf(
        UpdateType.CREATED to CreatedStrategy(),
        UpdateType.SHIPPED to ShippedStrategy(),
        UpdateType.LOCATION to LocationStrategy(),
        UpdateType.DELIVERED to DeliveredStrategy(),
        UpdateType.DELAYED to DelayedStrategy(),
        UpdateType.LOST to LostStrategy(),
        UpdateType.CANCELED to CanceledStrategy(),
        UpdateType.NOTE_ADDED to NoteAddedStrategy()
    )

    fun createShipment(
        id: String,
        update: ShippingUpdateDataModel,
    ): ShipmentDomainModel {
        // Determine type from update.otherInfo
        val type = update.otherInfo ?: "standard"
        val createdAt = update.updateTimestamp
        return when (type.lowercase()) {
            "express" -> ExpressShipment(id, strategyMap, createdAt)
            "overnight" -> OvernightShipment(id, strategyMap, createdAt)
            "bulk" -> BulkShipment(id, strategyMap, createdAt)
            else -> StandardShipment(id, strategyMap, createdAt)
        }
    }
}