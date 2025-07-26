package updatestrategies

import constants.ShipmentStatus
import constants.UpdateType
import models.datamodels.ShippingUpdateDataModel
import models.domainmodels.StandardShipment
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class CreatedStrategyTest {
    private lateinit var strategy: CreatedStrategy
    private lateinit var shipment: StandardShipment
    private val now = Instant.now()

    @BeforeEach
    fun setup() {
        strategy = CreatedStrategy()
        shipment = StandardShipment("id", emptyMap(), now)
    }

    @Test
    fun `apply sets status to CREATED and adds update`() {
        shipment.status = ShipmentStatus.SHIPPED
        val update = ShippingUpdateDataModel(UpdateType.CREATED, "id", now)
        strategy.apply(shipment, update)
        assertEquals(ShipmentStatus.CREATED, shipment.status)
        assertEquals(1, shipment.updates.size)
        val shippingUpdate = shipment.updates.first()
        assertEquals(ShipmentStatus.SHIPPED, shippingUpdate.previousStatus)
        assertEquals(ShipmentStatus.CREATED, shippingUpdate.newStatus)
        assertEquals(now, shippingUpdate.timestamp)
    }

    @Test
    fun `apply works when already CREATED`() {
        shipment.status = ShipmentStatus.CREATED
        val update = ShippingUpdateDataModel(UpdateType.CREATED, "id", now)
        strategy.apply(shipment, update)
        assertEquals(ShipmentStatus.CREATED, shipment.status)
        assertEquals(1, shipment.updates.size)
        val shippingUpdate = shipment.updates.first()
        assertEquals(ShipmentStatus.CREATED, shippingUpdate.previousStatus)
        assertEquals(ShipmentStatus.CREATED, shippingUpdate.newStatus)
    }
}