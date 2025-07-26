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

class DelayedStrategyTest {
    private lateinit var shipment: StandardShipment
    private lateinit var strategy: DelayedStrategy
    private lateinit var now: Instant

    @BeforeEach
    fun setup() {
        now = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        shipment = StandardShipment("id", mapOf(UpdateType.DELAYED to DelayedStrategy()), now)
        strategy = DelayedStrategy()
    }

    @Test
    fun `apply sets status to DELAYED and updates expectedDelivery when otherInfo is valid`() {
        shipment.status = ShipmentStatus.SHIPPED
        val newDelivery = now.plusSeconds(3600).truncatedTo(ChronoUnit.MILLIS)
        val update = ShippingUpdateDataModel(UpdateType.DELAYED, "id", now, newDelivery.toEpochMilli().toString())
        strategy.apply(shipment, update)
        assertEquals(ShipmentStatus.DELAYED, shipment.status)
        assertEquals(newDelivery, shipment.expectedDelivery)
        assertEquals(1, shipment.updates.size)
        val shippingUpdate = shipment.updates.first()
        assertEquals(ShipmentStatus.SHIPPED, shippingUpdate.previousStatus)
        assertEquals(ShipmentStatus.DELAYED, shippingUpdate.newStatus)
        assertEquals(now, shippingUpdate.timestamp)
    }

    @Test
    fun `apply sets expectedDelivery to null if otherInfo is not a number`() {
        shipment.status = ShipmentStatus.SHIPPED
        shipment.expectedDelivery = now.plusSeconds(1000)
        val update = ShippingUpdateDataModel(UpdateType.DELAYED, "id", now, "not_a_number")
        strategy.apply(shipment, update)
        assertEquals(ShipmentStatus.DELAYED, shipment.status)
        assertNull(shipment.expectedDelivery)
        assertEquals(1, shipment.updates.size)
    }

    @Test
    fun `apply sets expectedDelivery to null if otherInfo is null`() {
        shipment.status = ShipmentStatus.SHIPPED
        shipment.expectedDelivery = now.plusSeconds(1000)
        val update = ShippingUpdateDataModel(UpdateType.DELAYED, "id", now, null)
        strategy.apply(shipment, update)
        assertEquals(ShipmentStatus.DELAYED, shipment.status)
        assertNull(shipment.expectedDelivery)
        assertEquals(1, shipment.updates.size)
    }

    @Test
    fun `apply works when already DELAYED`() {
        shipment.status = ShipmentStatus.DELAYED
        val newDelivery = now.plusSeconds(7200).truncatedTo(ChronoUnit.MILLIS)
        val update = ShippingUpdateDataModel(UpdateType.DELAYED, "id", now, newDelivery.toEpochMilli().toString())
        strategy.apply(shipment, update)
        assertEquals(ShipmentStatus.DELAYED, shipment.status)
        assertEquals(newDelivery, shipment.expectedDelivery)
        assertEquals(1, shipment.updates.size)
        val shippingUpdate = shipment.updates.first()
        assertEquals(ShipmentStatus.DELAYED, shippingUpdate.previousStatus)
        assertEquals(ShipmentStatus.DELAYED, shippingUpdate.newStatus)
        assertEquals(now, shippingUpdate.timestamp)
    }
}