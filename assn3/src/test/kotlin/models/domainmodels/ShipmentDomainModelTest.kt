package models.domainmodels

import constants.ShipmentStatus
import constants.UpdateType
import models.datamodels.ShippingUpdateDataModel
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant

class DummyObserver : models.interfaces.IObserver<ShipmentDomainModel> {
    var updated = false
    override fun update(subject: ShipmentDomainModel) {
        updated = true
    }
}

class ShipmentDomainModelTest {
    private val strategies = mapOf(UpdateType.CREATED to updatestrategies.CreatedStrategy())
    private val now = Instant.now()

    @Test
    fun `StandardShipment accepts any update`() {
        val shipment = StandardShipment("id", strategies, now)
        val update = ShippingUpdateDataModel(UpdateType.CREATED, "id", now, null)
        assertDoesNotThrow { shipment.update(update) }
    }


    @Test
    fun `StandardShipment accepts any update and does not add abnormal events`() {
        val shipment = StandardShipment("id", strategies, now)
        val update = ShippingUpdateDataModel(UpdateType.CREATED, "id", now, null)
        shipment.update(update)
        assertTrue(shipment.toViewModel().abnormalEvents.isEmpty())
    }

    @Test
    fun `ExpressShipment abnormal event for late delivery`() {
        val shipment = ExpressShipment("id", strategies, now)
        val late = now.plusSeconds(4 * 24 * 3600)
        val update = ShippingUpdateDataModel(UpdateType.CREATED, "id", now, late.toEpochMilli().toString())
        shipment.update(update)
        assertTrue(shipment.toViewModel().abnormalEvents.any { it.contains("Express shipment delivery date is more than 3 days") })
    }

    @Test
    fun `ExpressShipment no abnormal event for on-time delivery`() {
        val shipment = ExpressShipment("id", strategies, now)
        val onTime = now.plusSeconds(2 * 24 * 3600)
        val update = ShippingUpdateDataModel(UpdateType.CREATED, "id", now, onTime.toEpochMilli().toString())
        shipment.update(update)
        assertTrue(shipment.toViewModel().abnormalEvents.isEmpty())
    }

    @Test
    fun `ExpressShipment no abnormal event if otherInfo is null or not a number`() {
        val shipment = ExpressShipment("id", strategies, now)
        val updateNull = ShippingUpdateDataModel(UpdateType.CREATED, "id", now, null)
        shipment.update(updateNull)
        assertTrue(shipment.toViewModel().abnormalEvents.isEmpty())

        val updateInvalid = ShippingUpdateDataModel(UpdateType.CREATED, "id", now, "not_a_number")
        shipment.update(updateInvalid)
        assertTrue(shipment.toViewModel().abnormalEvents.isEmpty())
    }

    @Test
    fun `OvernightShipment abnormal event for late delivery except DELAYED`() {
        val shipment = OvernightShipment("id", strategies, now)
        val late = now.plusSeconds(2 * 24 * 3600)
        val update = ShippingUpdateDataModel(UpdateType.CREATED, "id", now, late.toEpochMilli().toString())
        shipment.update(update)
        assertTrue(shipment.toViewModel().abnormalEvents.any { it.contains("Overnight shipment delivery date is more than 24 hours") })
    }

    @Test
    fun `OvernightShipment no abnormal event for DELAYED late delivery`() {
        val shipment = OvernightShipment("id", strategies + (UpdateType.DELAYED to updatestrategies.DelayedStrategy()), now)
        val late = now.plusSeconds(2 * 24 * 3600)
        val update = ShippingUpdateDataModel(UpdateType.DELAYED, "id", now, late.toEpochMilli().toString())
        shipment.update(update)
        assertTrue(shipment.toViewModel().abnormalEvents.isEmpty())
    }

    @Test
    fun `OvernightShipment no abnormal event for on-time delivery`() {
        val shipment = OvernightShipment("id", strategies, now)
        val onTime = now.plusSeconds(12 * 3600)
        val update = ShippingUpdateDataModel(UpdateType.CREATED, "id", now, onTime.toEpochMilli().toString())
        shipment.update(update)
        assertTrue(shipment.toViewModel().abnormalEvents.isEmpty())
    }

    @Test
    fun `OvernightShipment no abnormal event if otherInfo is null or not a number`() {
        val shipment = OvernightShipment("id", strategies, now)
        val updateNull = ShippingUpdateDataModel(UpdateType.CREATED, "id", now, null)
        shipment.update(updateNull)
        assertTrue(shipment.toViewModel().abnormalEvents.isEmpty())

        val updateInvalid = ShippingUpdateDataModel(UpdateType.CREATED, "id", now, "not_a_number")
        shipment.update(updateInvalid)
        assertTrue(shipment.toViewModel().abnormalEvents.isEmpty())
    }

    @Test
    fun `BulkShipment abnormal event for too-soon delivery`() {
        val shipment = BulkShipment("id", strategies, now)
        val soon = now.plusSeconds(2 * 24 * 3600)
        val update = ShippingUpdateDataModel(UpdateType.CREATED, "id", now, soon.toEpochMilli().toString())
        shipment.update(update)
        assertTrue(shipment.toViewModel().abnormalEvents.any { it.contains("Bulk shipment delivery date is sooner than 3 days") })
    }

    @Test
    fun `BulkShipment no abnormal event for on-time delivery`() {
        val shipment = BulkShipment("id", strategies, now)
        val onTime = now.plusSeconds(4 * 24 * 3600)
        val update = ShippingUpdateDataModel(UpdateType.CREATED, "id", now, onTime.toEpochMilli().toString())
        shipment.update(update)
        assertTrue(shipment.toViewModel().abnormalEvents.isEmpty())
    }

    @Test
    fun `BulkShipment no abnormal event if otherInfo is null or not a number`() {
        val shipment = BulkShipment("id", strategies, now)
        val updateNull = ShippingUpdateDataModel(UpdateType.CREATED, "id", now, null)
        shipment.update(updateNull)
        assertTrue(shipment.toViewModel().abnormalEvents.isEmpty())

        val updateInvalid = ShippingUpdateDataModel(UpdateType.CREATED, "id", now, "not_a_number")
        shipment.update(updateInvalid)
        assertTrue(shipment.toViewModel().abnormalEvents.isEmpty())
    }

    @Test
    fun `update throws for unknown update type`() {
        val shipment = StandardShipment("id", emptyMap(), now)
        val update = ShippingUpdateDataModel(UpdateType.SHIPPED, "id", now, null)
        assertThrows<IllegalArgumentException> { shipment.update(update) }
    }

    @Test
    fun `observer add, remove, and notify works`() {
        val shipment = StandardShipment("id", strategies, now)
        val observer = DummyObserver()
        shipment.addObserver(observer)
        val update = ShippingUpdateDataModel(UpdateType.CREATED, "id", now, null)
        shipment.update(update)
        assertTrue(observer.updated)
        observer.updated = false
        shipment.removeObserver(observer)
        shipment.update(update)
        assertFalse(observer.updated)
    }

    @Test
    fun `addNote and addUpdate work`() {
        val shipment = StandardShipment("id", strategies, now)
        shipment.addNote("test note")
        assertEquals(listOf("test note"), shipment.notes)
        val update = ShippingUpdateDomainModel(ShipmentStatus.CREATED, ShipmentStatus.SHIPPED, now)
        shipment.addUpdate(update)
        assertEquals(listOf(update), shipment.updates)
    }

    @Test
    fun `addAbnormalEvent adds event`() {
        val shipment = StandardShipment("id", strategies, now)
        shipment.addAbnormalEvent("event")
        assertEquals(listOf("event"), shipment.toViewModel().abnormalEvents)
    }
}