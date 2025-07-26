package updatestrategies

import constants.ShipmentStatus
import constants.UpdateType
import models.datamodels.ShippingUpdateDataModel
import models.domainmodels.StandardShipment
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class DeliveredStrategyTest {
    private lateinit var shipment: StandardShipment
    private lateinit var strategy: DeliveredStrategy
    private lateinit var now: Instant

    @BeforeEach
    fun setup() {
        now = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        shipment = StandardShipment("id", mapOf(UpdateType.DELIVERED to DeliveredStrategy()), now)
        strategy = DeliveredStrategy()
    }

    @Test
    fun `apply sets status to DELIVERED and adds update`() {
        shipment.status = ShipmentStatus.SHIPPED
        val update = ShippingUpdateDataModel(UpdateType.DELIVERED, "id", now, null)
        strategy.apply(shipment, update)
        assertEquals(ShipmentStatus.DELIVERED, shipment.status)
        assertEquals(1, shipment.updates.size)
        val shippingUpdate = shipment.updates.first()
        assertEquals(ShipmentStatus.SHIPPED, shippingUpdate.previousStatus)
        assertEquals(ShipmentStatus.DELIVERED, shippingUpdate.newStatus)
        assertEquals(now, shippingUpdate.timestamp)
    }

    @Test
    fun `apply works when already DELIVERED`() {
        shipment.status = ShipmentStatus.DELIVERED
        val update = ShippingUpdateDataModel(UpdateType.DELIVERED, "id", now, null)
        strategy.apply(shipment, update)
        assertEquals(ShipmentStatus.DELIVERED, shipment.status)
        assertEquals(1, shipment.updates.size)
        val shippingUpdate = shipment.updates.first()
        assertEquals(ShipmentStatus.DELIVERED, shippingUpdate.previousStatus)
        assertEquals(ShipmentStatus.DELIVERED, shippingUpdate.newStatus)
        assertEquals(now, shippingUpdate.timestamp)
    }
}