package updatestrategies

import constants.ShipmentStatus
import constants.UpdateType
import models.datamodels.ShippingUpdateDataModel
import models.domainmodels.StandardShipment
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class LostStrategyTest {
    private lateinit var strategy: LostStrategy
    private lateinit var shipment: StandardShipment
    private val now = Instant.now()

    @BeforeEach
    fun setup() {
        strategy = LostStrategy()
        shipment = StandardShipment("id", emptyMap(), now)
    }

    @Test
    fun `apply sets status to LOST and adds update`() {
        shipment.status = ShipmentStatus.SHIPPED
        val update = ShippingUpdateDataModel(UpdateType.LOST, "id", now)
        strategy.apply(shipment, update)
        assertEquals(ShipmentStatus.LOST, shipment.status)
        assertEquals(1, shipment.updates.size)
        val shippingUpdate = shipment.updates.first()
        assertEquals(ShipmentStatus.SHIPPED, shippingUpdate.previousStatus)
        assertEquals(ShipmentStatus.LOST, shippingUpdate.newStatus)
        assertEquals(now, shippingUpdate.timestamp)
    }

    @Test
    fun `apply works when already LOST`() {
        shipment.status = ShipmentStatus.LOST
        val update = ShippingUpdateDataModel(UpdateType.LOST, "id", now)
        strategy.apply(shipment, update)
        assertEquals(ShipmentStatus.LOST, shipment.status)
        assertEquals(1, shipment.updates.size)
        val shippingUpdate = shipment.updates.first()
        assertEquals(ShipmentStatus.LOST, shippingUpdate.previousStatus)
        assertEquals(ShipmentStatus.LOST, shippingUpdate.newStatus)
    }
}