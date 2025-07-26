package models.viewmodels

import constants.ShipmentStatus
import kotlinx.serialization.json.Json
import models.domainmodels.ShippingUpdateDomainModel
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant

class ShipmentViewModelTest {

    private val now = Instant.now()
    private val update = ShippingUpdateDomainModel(
        previousStatus = ShipmentStatus.CREATED,
        newStatus = ShipmentStatus.SHIPPED,
        timestamp = now
    )

    @Test
    fun `constructor sets all fields correctly`() {
        val model = ShipmentViewModel(
            id = "id1",
            status = ShipmentStatus.SHIPPED,
            currentLocation = "Warehouse",
            expectedDelivery = now,
            notes = listOf("note1", "note2"),
            updates = listOf(update),
            abnormalEvents = listOf("event1"),
            shipmentType = "express"
        )
        assertEquals("id1", model.id)
        assertEquals(ShipmentStatus.SHIPPED, model.status)
        assertEquals("Warehouse", model.currentLocation)
        assertEquals(now, model.expectedDelivery)
        assertEquals(listOf("note1", "note2"), model.notes)
        assertEquals(listOf(update), model.updates)
        assertEquals(listOf("event1"), model.abnormalEvents)
        assertEquals("express", model.shipmentType)
    }

    @Test
    fun `equality and hashCode work as expected`() {
        val a = ShipmentViewModel("id", ShipmentStatus.CREATED, "", now, emptyList(), emptyList(), emptyList(), "standard")
        val b = ShipmentViewModel("id", ShipmentStatus.CREATED, "", now, emptyList(), emptyList(), emptyList(), "standard")
        val c = ShipmentViewModel("id2", ShipmentStatus.SHIPPED, "loc", now, listOf("n"), listOf(update), listOf("e"), "express")
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertNotEquals(a, c)
    }

    @Test
    fun `toString returns expected format`() {
        val model = ShipmentViewModel("id", ShipmentStatus.CREATED, "loc", now, listOf("n"), listOf(update), listOf("e"), "standard")
        val str = model.toString()
        assertTrue(str.contains("id=id"))
        assertTrue(str.contains("status=CREATED"))
        assertTrue(str.contains("currentLocation=loc"))
        assertTrue(str.contains("shipmentType=standard"))
    }

    @Test
    fun `serialization and deserialization with kotlinx works`() {
        val model = ShipmentViewModel(
            id = "id",
            status = ShipmentStatus.DELIVERED,
            currentLocation = "loc",
            expectedDelivery = now,
            notes = listOf("note"),
            updates = listOf(update),
            abnormalEvents = listOf("event"),
            shipmentType = "bulk"
        )
        val jsonStr = Json.encodeToString(ShipmentViewModel.serializer(), model)
        val decoded = Json.decodeFromString(ShipmentViewModel.serializer(), jsonStr)

        assertEquals(model.id, decoded.id)
        assertEquals(model.status, decoded.status)
        assertEquals(model.currentLocation, decoded.currentLocation)
        assertEquals(model.expectedDelivery?.toEpochMilli(), decoded.expectedDelivery?.toEpochMilli())
        assertEquals(model.notes, decoded.notes)
        assertEquals(model.abnormalEvents, decoded.abnormalEvents)
        assertEquals(model.shipmentType, decoded.shipmentType)

        // Compare updates field-by-field to avoid Instant precision issues
        assertEquals(model.updates.size, decoded.updates.size)
        for ((expected, actual) in model.updates.zip(decoded.updates)) {
            assertEquals(expected.previousStatus, actual.previousStatus)
            assertEquals(expected.newStatus, actual.newStatus)
            assertEquals(expected.timestamp.toEpochMilli(), actual.timestamp.toEpochMilli())
        }
    }

    @Test
    fun `null expectedDelivery is handled correctly`() {
        val model = ShipmentViewModel(
            id = "id",
            status = ShipmentStatus.LOST,
            currentLocation = "",
            expectedDelivery = null,
            notes = emptyList(),
            updates = emptyList(),
            abnormalEvents = emptyList(),
            shipmentType = "overnight"
        )
        val jsonStr = Json.encodeToString(ShipmentViewModel.serializer(), model)
        val decoded = Json.decodeFromString(ShipmentViewModel.serializer(), jsonStr)
        assertNull(decoded.expectedDelivery)
    }

    @Test
    fun `empty lists are handled correctly`() {
        val model = ShipmentViewModel(
            id = "id",
            status = ShipmentStatus.CANCELED,
            currentLocation = "",
            expectedDelivery = now,
            notes = emptyList(),
            updates = emptyList(),
            abnormalEvents = emptyList(),
            shipmentType = "standard"
        )
        val jsonStr = Json.encodeToString(ShipmentViewModel.serializer(), model)
        val decoded = Json.decodeFromString(ShipmentViewModel.serializer(), jsonStr)
        assertTrue(decoded.notes.isEmpty())
        assertTrue(decoded.updates.isEmpty())
        assertTrue(decoded.abnormalEvents.isEmpty())
    }

    @Test
    fun `equals returns false for null and different type`() {
        val a = ShipmentViewModel("id", ShipmentStatus.CREATED, "", now, emptyList(), emptyList(), emptyList(), "standard")
        assertNotEquals(a, null)
        assertNotEquals(a, "not a ShipmentViewModel")
    }

    @Test
    fun `equals returns true for self`() {
        val a = ShipmentViewModel("id", ShipmentStatus.CREATED, "", now, emptyList(), emptyList(), emptyList(), "standard")
        assertEquals(a, a)
    }
}