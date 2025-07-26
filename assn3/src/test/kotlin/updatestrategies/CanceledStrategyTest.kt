package updatestrategies

import constants.ShipmentStatus
import constants.UpdateType
import models.datamodels.ShippingUpdateDataModel
import models.domainmodels.StandardShipment
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class CanceledStrategyTest {

    private lateinit var strategy: CanceledStrategy
    private lateinit var shipment: StandardShipment
    private val now = Instant.now()

    @BeforeEach
    fun setup() {
        strategy = CanceledStrategy()
        shipment = StandardShipment("id", emptyMap(), now)
    }

    @Test
    fun `apply sets status to CANCELED and adds update`() {
        shipment.status = ShipmentStatus.SHIPPED
        val update = ShippingUpdateDataModel(UpdateType.CANCELED, "id", now)
        strategy.apply(shipment, update)

        assertEquals(ShipmentStatus.CANCELED, shipment.status)
        assertEquals(1, shipment.updates.size)
        val shippingUpdate = shipment.updates.first()
        assertEquals(ShipmentStatus.SHIPPED, shippingUpdate.previousStatus)
        assertEquals(ShipmentStatus.CANCELED, shippingUpdate.newStatus)
        assertEquals(now, shippingUpdate.timestamp)
    }

    @Test
    fun `apply works when shipment is already CANCELED`() {
        shipment.status = ShipmentStatus.CANCELED
        val update = ShippingUpdateDataModel(UpdateType.CANCELED, "id", now)
        strategy.apply(shipment, update)

        assertEquals(ShipmentStatus.CANCELED, shipment.status)
        assertEquals(1, shipment.updates.size)
        val shippingUpdate = shipment.updates.first()
        assertEquals(ShipmentStatus.CANCELED, shippingUpdate.previousStatus)
        assertEquals(ShipmentStatus.CANCELED, shippingUpdate.newStatus)
    }

    @Test
    fun `apply can be called multiple times and adds multiple updates`() {
        shipment.status = ShipmentStatus.SHIPPED
        val update1 = ShippingUpdateDataModel(UpdateType.CANCELED, "id", now)
        val update2 = ShippingUpdateDataModel(UpdateType.CANCELED, "id", now.plusSeconds(10))
        strategy.apply(shipment, update1)
        strategy.apply(shipment, update2)

        assertEquals(ShipmentStatus.CANCELED, shipment.status)
        assertEquals(2, shipment.updates.size)
        assertEquals(now, shipment.updates[0].timestamp)
        assertEquals(now.plusSeconds(10), shipment.updates[1].timestamp)
    }
}