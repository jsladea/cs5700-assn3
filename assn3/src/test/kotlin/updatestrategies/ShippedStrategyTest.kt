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

class ShippedStrategyTest {
    private lateinit var shipment: StandardShipment
    private lateinit var strategy: ShippedStrategy
    private lateinit var now: Instant

    @BeforeEach
    fun setup() {
        now = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        shipment = StandardShipment("id", mapOf(UpdateType.SHIPPED to ShippedStrategy()), now)
        strategy = ShippedStrategy()
    }

    @Test
    fun `apply sets status to SHIPPED, updates expectedDelivery, and adds update when otherInfo is valid`() {
        shipment.status = ShipmentStatus.CREATED
        val newDelivery = now.plusSeconds(3600).truncatedTo(ChronoUnit.MILLIS)
        val update = ShippingUpdateDataModel(UpdateType.SHIPPED, "id", now, newDelivery.toEpochMilli().toString())
        strategy.apply(shipment, update)
        assertEquals(ShipmentStatus.SHIPPED, shipment.status)
        assertEquals(newDelivery, shipment.expectedDelivery)
        assertEquals(1, shipment.updates.size)
        val shippingUpdate = shipment.updates.first()
        assertEquals(ShipmentStatus.CREATED, shippingUpdate.previousStatus)
        assertEquals(ShipmentStatus.SHIPPED, shippingUpdate.newStatus)
        assertEquals(now, shippingUpdate.timestamp)
    }

    @Test
    fun `apply sets expectedDelivery to null if otherInfo is not a number`() {
        shipment.status = ShipmentStatus.CREATED
        shipment.expectedDelivery = now.plusSeconds(1000)
        val update = ShippingUpdateDataModel(UpdateType.SHIPPED, "id", now, "not_a_number")
        strategy.apply(shipment, update)
        assertEquals(ShipmentStatus.SHIPPED, shipment.status)
        assertNull(shipment.expectedDelivery)
        assertEquals(1, shipment.updates.size)
        val shippingUpdate = shipment.updates.first()
        assertEquals(ShipmentStatus.CREATED, shippingUpdate.previousStatus)
        assertEquals(ShipmentStatus.SHIPPED, shippingUpdate.newStatus)
        assertEquals(now, shippingUpdate.timestamp)
    }

    @Test
    fun `apply works when already SHIPPED`() {
        shipment.status = ShipmentStatus.SHIPPED
        val newDelivery = now.plusSeconds(7200).truncatedTo(ChronoUnit.MILLIS)
        val update = ShippingUpdateDataModel(UpdateType.SHIPPED, "id", now, newDelivery.toEpochMilli().toString())
        strategy.apply(shipment, update)
        assertEquals(ShipmentStatus.SHIPPED, shipment.status)
        assertEquals(newDelivery, shipment.expectedDelivery)
        assertEquals(1, shipment.updates.size)
        val shippingUpdate = shipment.updates.first()
        assertEquals(ShipmentStatus.SHIPPED, shippingUpdate.previousStatus)
        assertEquals(ShipmentStatus.SHIPPED, shippingUpdate.newStatus)
        assertEquals(now, shippingUpdate.timestamp)
    }
}