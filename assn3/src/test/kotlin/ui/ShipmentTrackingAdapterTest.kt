package ui

import models.domainmodels.StandardShipment
import models.domainmodels.ShipmentDomainModel
import constants.UpdateType
import constants.ShipmentStatus
import models.viewmodels.ShipmentViewModel
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import server.repositories.ShipmentRepository
import java.time.Instant

class ShipmentTrackingAdapterTest {

    private val now = Instant.now()
    private var lastUpdate: Pair<String, ShipmentViewModel>? = null
    private lateinit var adapter: ShipmentTrackingAdapter

    private fun makeShipment(id: String = "id"): ShipmentDomainModel {
        val strategies = emptyMap<UpdateType, updatestrategies.IShipmentUpdateStrategy>()
        return StandardShipment(id, strategies, now)
    }

    @BeforeEach
    fun setup() {
        ShipmentRepository.clear()
        lastUpdate = null
        adapter = ShipmentTrackingAdapter { id, vm -> lastUpdate = id to vm }
    }

    @Test
    fun `trackShipment adds observer and calls callback`() {
        val shipment = makeShipment("track1")
        ShipmentRepository.upsertShipment(shipment)
        adapter.trackShipment("track1")
        assertTrue(lastUpdate != null)
        assertEquals("track1", lastUpdate!!.first)
        // Should throw if tracking again
        val ex = assertThrows<IllegalStateException> { adapter.trackShipment("track1") }
        assertTrue(ex.message!!.contains("already being tracked"))
    }

    @Test
    fun `trackShipment throws if shipment does not exist`() {
        val ex = assertThrows<NoSuchElementException> { adapter.trackShipment("nope") }
        assertTrue(ex.message!!.contains("does not exist"))
    }

    @Test
    fun `removeShipmentTracking removes observer and does nothing if not tracked`() {
        val shipment = makeShipment("rem1")
        ShipmentRepository.upsertShipment(shipment)
        // Not tracked yet, should not throw
        assertDoesNotThrow { adapter.removeShipmentTracking("rem1") }
        // Track and then remove
        adapter.trackShipment("rem1")
        assertTrue(lastUpdate != null)
        adapter.removeShipmentTracking("rem1")
        // Removing again should not throw
        assertDoesNotThrow { adapter.removeShipmentTracking("rem1") }
    }

    @Test
    fun `removeShipmentTracking does nothing if shipment does not exist`() {
        // Not tracked and does not exist
        assertDoesNotThrow { adapter.removeShipmentTracking("nope") }
    }

    @Test
    fun `removeShipmentTracking does nothing if shipment is tracked but no longer exists`() {
        val shipment = makeShipment("gone1")
        ShipmentRepository.upsertShipment(shipment)
        adapter.trackShipment("gone1")
        // Remove from repository
        ShipmentRepository.clear()
        // Should not throw
        assertDoesNotThrow { adapter.removeShipmentTracking("gone1") }
    }

    @Test
    fun `update calls callback with correct shipment`() {
        val shipment = makeShipment("up1")
        ShipmentRepository.upsertShipment(shipment)
        adapter.trackShipment("up1")
        // Simulate update
        shipment.status = ShipmentStatus.SHIPPED
        adapter.update(shipment)
        assertEquals("up1", lastUpdate!!.first)
        assertEquals(ShipmentStatus.SHIPPED, lastUpdate!!.second.status)
    }

    @Test
    fun `update calls callback even if shipment is not tracked`() {
        val shipment = makeShipment("notTracked")
        ShipmentRepository.upsertShipment(shipment)
        // Not tracked, but should still call callback
        adapter.update(shipment)
        assertEquals("notTracked", lastUpdate!!.first)
    }

    @Test
    fun `trackShipment and removeShipmentTracking work independently for multiple shipments`() {
        val s1 = makeShipment("a")
        val s2 = makeShipment("b")
        ShipmentRepository.upsertShipment(s1)
        ShipmentRepository.upsertShipment(s2)
        adapter.trackShipment("a")
        assertEquals("a", lastUpdate!!.first)
        adapter.trackShipment("b")
        assertEquals("b", lastUpdate!!.first)
        adapter.removeShipmentTracking("a")
        // Removing a does not affect b
        s2.status = ShipmentStatus.SHIPPED
        adapter.update(s2)
        assertEquals("b", lastUpdate!!.first)
        assertEquals(ShipmentStatus.SHIPPED, lastUpdate!!.second.status)
    }
}