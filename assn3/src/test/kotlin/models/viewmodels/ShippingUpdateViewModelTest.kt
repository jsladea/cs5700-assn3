package models.viewmodels

import constants.UpdateType
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant

class ShippingUpdateViewModelTest {

    private val now = Instant.now()
    private val nowMillis = now.toEpochMilli().toString()

    @Test
    fun `constructor sets all fields correctly`() {
        val model = ShippingUpdateViewModel(
            updateType = "CREATED",
            id = "id1",
            timestamp = nowMillis,
            otherInfo = "info"
        )
        assertEquals("CREATED", model.updateType)
        assertEquals("id1", model.id)
        assertEquals(nowMillis, model.timestamp)
        assertEquals("info", model.otherInfo)
    }

    @Test
    fun `equality and hashCode work as expected`() {
        val a = ShippingUpdateViewModel("CREATED", "id", nowMillis, "x")
        val b = ShippingUpdateViewModel("CREATED", "id", nowMillis, "x")
        val c = ShippingUpdateViewModel("SHIPPED", "id2", nowMillis, null)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertNotEquals(a, c)
    }

    @Test
    fun `toString returns expected format`() {
        val model = ShippingUpdateViewModel("CREATED", "id", nowMillis, "info")
        val str = model.toString()
        assertTrue(str.contains("updateType=CREATED"))
        assertTrue(str.contains("id=id"))
        assertTrue(str.contains("timestamp=$nowMillis"))
        assertTrue(str.contains("otherInfo=info"))
    }

    @Test
    fun `serialization and deserialization with kotlinx works`() {
        val model = ShippingUpdateViewModel("SHIPPED", "id", nowMillis, "extra")
        val jsonStr = Json.encodeToString(ShippingUpdateViewModel.serializer(), model)
        val decoded = Json.decodeFromString(ShippingUpdateViewModel.serializer(), jsonStr)
        assertEquals(model, decoded)
    }

    @Test
    fun `serialization and deserialization with null otherInfo works`() {
        val model = ShippingUpdateViewModel("DELIVERED", "id", nowMillis, null)
        val jsonStr = Json.encodeToString(ShippingUpdateViewModel.serializer(), model)
        val decoded = Json.decodeFromString(ShippingUpdateViewModel.serializer(), jsonStr)
        assertEquals(model, decoded)
        assertNull(decoded.otherInfo)
    }

    @Test
    fun `toDataModel returns correct data model for valid input`() {
        val model = ShippingUpdateViewModel("CREATED", "id", nowMillis, "info")
        val data = model.toDataModel()
        assertEquals(UpdateType.CREATED, data.type)
        assertEquals("id", data.id)
        assertEquals(Instant.ofEpochMilli(nowMillis.toLong()), data.updateTimestamp)
        assertEquals("info", data.otherInfo)
    }

    @Test
    fun `toDataModel throws for invalid updateType`() {
        val model = ShippingUpdateViewModel("INVALID_TYPE", "id", nowMillis, null)
        val ex = assertThrows(IllegalArgumentException::class.java) {
            model.toDataModel()
        }
        assertTrue(ex.message!!.contains("Invalid update type"))
    }

    @Test
    fun `toDataModel throws for invalid timestamp`() {
        val model = ShippingUpdateViewModel("CREATED", "id", "not_a_number", null)
        val ex = assertThrows(IllegalArgumentException::class.java) {
            model.toDataModel()
        }
        assertTrue(ex.message!!.contains("Invalid timestamp"))
    }

    @Test
    fun `equals returns false for null and different type`() {
        val a = ShippingUpdateViewModel("CREATED", "id", nowMillis, null)
        assertNotEquals(a, null)
        assertNotEquals(a, "not a ShippingUpdateViewModel")
    }

    @Test
    fun `equals returns true for self`() {
        val a = ShippingUpdateViewModel("CREATED", "id", nowMillis, null)
        assertEquals(a, a)
    }
}