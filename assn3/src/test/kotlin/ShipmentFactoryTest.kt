import constants.UpdateType
import models.datamodels.ShippingUpdateDataModel
import models.domainmodels.BulkShipment
import models.domainmodels.ExpressShipment
import models.domainmodels.OvernightShipment
import models.domainmodels.ShipmentDomainModel
import models.domainmodels.StandardShipment
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant

class ShipmentFactoryTest {

    private val now = Instant.now()

    private fun makeUpdate(type: String? = null): ShippingUpdateDataModel {
        return ShippingUpdateDataModel(
            type = UpdateType.CREATED,
            id = "id",
            updateTimestamp = now,
            otherInfo = type
        )
    }

    @Test
    fun `creates StandardShipment if otherInfo is null`() {
        val shipment = ShipmentFactory.createShipment("id", makeUpdate(null))
        assertTrue(shipment is StandardShipment)
        assertEquals("id", shipment.id)
        assertEquals(now, shipment.creationTime)
    }

    @Test
    fun `creates StandardShipment if otherInfo is unknown`() {
        val shipment = ShipmentFactory.createShipment("id", makeUpdate("unknown"))
        assertTrue(shipment is StandardShipment)
    }

    @Test
    fun `creates StandardShipment if otherInfo is standard`() {
        val shipment = ShipmentFactory.createShipment("id", makeUpdate("standard"))
        assertTrue(shipment is StandardShipment)
    }

    @Test
    fun `creates ExpressShipment if otherInfo is express`() {
        val shipment = ShipmentFactory.createShipment("id", makeUpdate("express"))
        assertTrue(shipment is ExpressShipment)
    }

    @Test
    fun `creates OvernightShipment if otherInfo is overnight`() {
        val shipment = ShipmentFactory.createShipment("id", makeUpdate("overnight"))
        assertTrue(shipment is OvernightShipment)
    }

    @Test
    fun `creates BulkShipment if otherInfo is bulk`() {
        val shipment = ShipmentFactory.createShipment("id", makeUpdate("bulk"))
        assertTrue(shipment is BulkShipment)
    }

    @Test
    fun `otherInfo is case-insensitive`() {
        val express = ShipmentFactory.createShipment("id", makeUpdate("ExPrEsS"))
        val overnight = ShipmentFactory.createShipment("id", makeUpdate("OVERNIGHT"))
        val bulk = ShipmentFactory.createShipment("id", makeUpdate("BuLk"))
        assertTrue(express is ExpressShipment)
        assertTrue(overnight is OvernightShipment)
        assertTrue(bulk is BulkShipment)
    }

    @Test
    fun `creationTime is set from update timestamp`() {
        val customTime = now.plusSeconds(1234)
        val update = ShippingUpdateDataModel(UpdateType.CREATED, "id", customTime, "express")
        val shipment = ShipmentFactory.createShipment("id", update)
        assertEquals(customTime, shipment.creationTime)
    }
}