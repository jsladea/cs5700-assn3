package server.repositories

import constants.ShipmentStatus
import constants.UpdateType
import models.domainmodels.ShipmentDomainModel
import models.domainmodels.StandardShipment
import models.domainmodels.ShippingUpdateDomainModel
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.Instant

class ShipmentRepositoryTest {

    private fun makeShipment(id: String = "id"): ShipmentDomainModel {
        val strategies = emptyMap<UpdateType, updatestrategies.IShipmentUpdateStrategy>()
        return StandardShipment(id, strategies, Instant.now())
    }

    @BeforeEach
    fun setup() {
        ShipmentRepository.clear()
    }

    @Test
    fun `getShipment returns null if not present`() {
        assertNull(ShipmentRepository.getShipment("not-exist"))
    }

    @Test
    fun `upsertShipment inserts new shipment`() {
        val shipment = makeShipment("id1")
        ShipmentRepository.upsertShipment(shipment)
        val result = ShipmentRepository.getShipment("id1")
        assertNotNull(result)
        assertEquals("id1", result!!.id)
    }

    @Test
    fun `upsertShipment updates existing shipment`() {
        val shipment1 = makeShipment("id2")
        ShipmentRepository.upsertShipment(shipment1)
        val shipment2 = makeShipment("id2")
        shipment2.status = ShipmentStatus.SHIPPED
        ShipmentRepository.upsertShipment(shipment2)
        val result = ShipmentRepository.getShipment("id2")
        assertNotNull(result)
        assertEquals(ShipmentStatus.SHIPPED, result!!.status)
    }

    @Test
    fun `clear removes all shipments`() {
        val shipment = makeShipment("id3")
        ShipmentRepository.upsertShipment(shipment)
        assertNotNull(ShipmentRepository.getShipment("id3"))
        ShipmentRepository.clear()
        assertNull(ShipmentRepository.getShipment("id3"))
    }

    @Test
    fun `multiple shipments can be stored and retrieved independently`() {
        val s1 = makeShipment("a")
        val s2 = makeShipment("b")
        ShipmentRepository.upsertShipment(s1)
        ShipmentRepository.upsertShipment(s2)
        assertEquals("a", ShipmentRepository.getShipment("a")!!.id)
        assertEquals("b", ShipmentRepository.getShipment("b")!!.id)
    }
}