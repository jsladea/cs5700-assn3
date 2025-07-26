package server.services

import models.viewmodels.ShippingUpdateViewModel
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import server.repositories.ShipmentRepository
import java.time.Instant

class ShipmentServiceTest {

    private fun makeViewModel(
        id: String = "test-id",
        updateType: String = "CREATED",
        timestamp: String = Instant.now().toEpochMilli().toString(),
        otherInfo: String? = null
    ) = ShippingUpdateViewModel(updateType, id, timestamp, otherInfo)

    @BeforeEach
    fun setup() {
        ShipmentRepository.clear()
    }

    @Test
    fun `createShipment - success`() {
        val viewModel = makeViewModel()
        val result = ShipmentService.createShipment(viewModel)
        assertEquals("test-id", result.id)
        assertEquals("CREATED", result.status.name)
    }

    @Test
    fun `createShipment - already exists throws`() {
        val viewModel = makeViewModel()
        ShipmentService.createShipment(viewModel)
        val ex = assertThrows<IllegalArgumentException> {
            ShipmentService.createShipment(viewModel)
        }
        assertTrue(ex.message!!.contains("already exists"))
    }

    @Test
    fun `createShipment - invalid timestamp throws`() {
        val viewModel = makeViewModel(timestamp = "not_a_number")
        val ex = assertThrows<IllegalArgumentException> {
            ShipmentService.createShipment(viewModel)
        }
        assertTrue(ex.message!!.contains("Invalid timestamp"))
    }

    @Test
    fun `updateShipment - success`() {
        val create = makeViewModel()
        ShipmentService.createShipment(create)
        val update = makeViewModel(updateType = "SHIPPED")
        val result = ShipmentService.updateShipment(update)
        assertNotNull(result)
        assertEquals("SHIPPED", result!!.status.name)
    }

    @Test
    fun `updateShipment - not found returns null`() {
        val update = makeViewModel(id = "does-not-exist", updateType = "SHIPPED")
        val result = ShipmentService.updateShipment(update)
        assertNull(result)
    }

    @Test
    fun `updateShipment - unknown update type throws`() {
        val create = makeViewModel()
        ShipmentService.createShipment(create)
        val update = makeViewModel(updateType = "UNKNOWN_TYPE")
        val ex = assertThrows<IllegalArgumentException> {
            ShipmentService.updateShipment(update)
        }
        assertTrue(ex.message!!.contains("Invalid update type") || ex.message!!.contains("Unknown update type"))
    }

    @Test
    fun `updateShipment - invalid timestamp throws`() {
        val create = makeViewModel()
        ShipmentService.createShipment(create)
        val update = makeViewModel(updateType = "SHIPPED", timestamp = "not_a_number")
        val ex = assertThrows<IllegalArgumentException> {
            ShipmentService.updateShipment(update)
        }
        assertTrue(ex.message!!.contains("Invalid timestamp"))
    }

    @Test
    fun `getShipment - found returns view model`() {
        val create = makeViewModel()
        ShipmentService.createShipment(create)
        val result = ShipmentService.getShipment("test-id")
        assertNotNull(result)
        assertEquals("test-id", result!!.id)
    }

    @Test
    fun `getShipment - not found returns null`() {
        val result = ShipmentService.getShipment("does-not-exist")
        assertNull(result)
    }
}