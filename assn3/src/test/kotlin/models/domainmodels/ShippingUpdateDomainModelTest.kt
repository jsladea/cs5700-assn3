package models.domainmodels

import constants.ShipmentStatus
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant

class ShippingUpdateDomainModelTest {

    private val now = Instant.now()


    @Test
    fun `constructor sets all fields correctly`() {
        val model = ShippingUpdateDomainModel(
            previousStatus = ShipmentStatus.CREATED,
            newStatus = ShipmentStatus.SHIPPED,
            timestamp = now
        )
        assertEquals(ShipmentStatus.CREATED, model.previousStatus)
        assertEquals(ShipmentStatus.SHIPPED, model.newStatus)
        assertEquals(now, model.timestamp)
    }

    @Test
    fun `equality and hashCode work as expected`() {
        val a = ShippingUpdateDomainModel(ShipmentStatus.CREATED, ShipmentStatus.SHIPPED, now)
        val b = ShippingUpdateDomainModel(ShipmentStatus.CREATED, ShipmentStatus.SHIPPED, now)
        val c = ShippingUpdateDomainModel(ShipmentStatus.SHIPPED, ShipmentStatus.DELIVERED, now)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertNotEquals(a, c)
    }

    @Test
    fun `toString returns expected format`() {
        val model = ShippingUpdateDomainModel(ShipmentStatus.CREATED, ShipmentStatus.SHIPPED, now)
        val str = model.toString()
        assertTrue(str.contains("previousStatus=CREATED"))
        assertTrue(str.contains("newStatus=SHIPPED"))
        assertTrue(str.contains("timestamp="))
    }

    @Test
    fun `serialization and deserialization with kotlinx works`() {
        val model = ShippingUpdateDomainModel(ShipmentStatus.CREATED, ShipmentStatus.SHIPPED, now)
        val json = Json.encodeToString(ShippingUpdateDomainModel.serializer(), model)
        val decoded = Json.decodeFromString(ShippingUpdateDomainModel.serializer(), json)
        // Compare fields instead of whole object to avoid Instant precision issues
        assertEquals(model.previousStatus, decoded.previousStatus)
        assertEquals(model.newStatus, decoded.newStatus)
        assertEquals(model.timestamp.toEpochMilli(), decoded.timestamp.toEpochMilli())
    }

    @Test
    fun `different timestamps are not equal`() {
        val a = ShippingUpdateDomainModel(ShipmentStatus.CREATED, ShipmentStatus.SHIPPED, now)
        val b = ShippingUpdateDomainModel(ShipmentStatus.CREATED, ShipmentStatus.SHIPPED, now.plusSeconds(1))
        assertNotEquals(a, b)
    }

    @Test
    fun `equals returns false for null and different type`() {
        val a = ShippingUpdateDomainModel(ShipmentStatus.CREATED, ShipmentStatus.SHIPPED, now)
        assertNotEquals(a, null)
        assertNotEquals(a, "not a ShippingUpdateDomainModel")
    }

    @Test
    fun `equals returns true for self`() {
        val a = ShippingUpdateDomainModel(ShipmentStatus.CREATED, ShipmentStatus.SHIPPED, now)
        assertEquals(a, a)
    }
}