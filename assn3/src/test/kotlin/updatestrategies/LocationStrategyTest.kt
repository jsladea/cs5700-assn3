package updatestrategies

import constants.ShipmentStatus
import constants.UpdateType
import models.datamodels.ShippingUpdateDataModel
import models.domainmodels.StandardShipment
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class LocationStrategyTest {
    private lateinit var strategy: LocationStrategy
    private lateinit var shipment: StandardShipment
    private val now = Instant.now()

    @BeforeEach
    fun setup() {
        strategy = LocationStrategy()
        shipment = StandardShipment("id", emptyMap(), now)
    }

    @Test
    fun `apply updates currentLocation if otherInfo is present`() {
        shipment.currentLocation = "OldLoc"
        val update = ShippingUpdateDataModel(UpdateType.LOCATION, "id", now, "NewLoc")
        strategy.apply(shipment, update)
        assertEquals("NewLoc", shipment.currentLocation)
    }

    @Test
    fun `apply does not update currentLocation if otherInfo is null`() {
        shipment.currentLocation = "OldLoc"
        val update = ShippingUpdateDataModel(UpdateType.LOCATION, "id", now, null)
        strategy.apply(shipment, update)
        assertEquals("OldLoc", shipment.currentLocation)
    }

    @Test
    fun `apply sets status to SHIPPED if status was LOST`() {
        shipment.status = ShipmentStatus.LOST
        val update = ShippingUpdateDataModel(UpdateType.LOCATION, "id", now, "Loc")
        strategy.apply(shipment, update)
        assertEquals(ShipmentStatus.SHIPPED, shipment.status)
    }

    @Test
    fun `apply does not change status if not LOST`() {
        shipment.status = ShipmentStatus.SHIPPED
        val update = ShippingUpdateDataModel(UpdateType.LOCATION, "id", now, "Loc")
        strategy.apply(shipment, update)
        assertEquals(ShipmentStatus.SHIPPED, shipment.status)
    }
}