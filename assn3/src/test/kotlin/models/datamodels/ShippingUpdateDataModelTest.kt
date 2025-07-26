package models.datamodels

import constants.UpdateType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant

class ShippingUpdateDataModelTest {

    private val now = Instant.now()
    private val id = "shipment-123"
    private val otherInfo = "info"

    @Test
    fun `constructor sets all fields correctly`() {
        val model = ShippingUpdateDataModel(UpdateType.SHIPPED, id, now, otherInfo)
        assertEquals(UpdateType.SHIPPED, model.type)
        assertEquals(id, model.id)
        assertEquals(now, model.updateTimestamp)
        assertEquals(otherInfo, model.otherInfo)
    }

    @Test
    fun `constructor works with null otherInfo`() {
        val model = ShippingUpdateDataModel(UpdateType.CREATED, id, now, null)
        assertEquals(UpdateType.CREATED, model.type)
        assertEquals(id, model.id)
        assertEquals(now, model.updateTimestamp)
        assertNull(model.otherInfo)
    }

    @Test
    fun `equality and hashCode work as expected`() {
        val a = ShippingUpdateDataModel(UpdateType.SHIPPED, id, now, otherInfo)
        val b = ShippingUpdateDataModel(UpdateType.SHIPPED, id, now, otherInfo)
        val c = ShippingUpdateDataModel(UpdateType.CREATED, id, now, otherInfo)
        val d = ShippingUpdateDataModel(UpdateType.SHIPPED, "other", now, otherInfo)
        val e = ShippingUpdateDataModel(UpdateType.SHIPPED, id, now.plusSeconds(1), otherInfo)
        val f = ShippingUpdateDataModel(UpdateType.SHIPPED, id, now, "different")
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertNotEquals(a, c)
        assertNotEquals(a, d)
        assertNotEquals(a, e)
        assertNotEquals(a, f)
    }

    @Test
    fun `toString returns expected format`() {
        val model = ShippingUpdateDataModel(UpdateType.LOST, id, now, otherInfo)
        val str = model.toString()
        assertTrue(str.contains("type=LOST"))
        assertTrue(str.contains("id=shipment-123"))
        assertTrue(str.contains("updateTimestamp="))
        assertTrue(str.contains("otherInfo=info"))
    }

    @Test
    fun `equals returns false for null and different type`() {
        val model = ShippingUpdateDataModel(UpdateType.SHIPPED, id, now, otherInfo)
        assertNotEquals(model, null)
        assertNotEquals(model, "not a ShippingUpdateDataModel")
    }

    @Test
    fun `equals returns true for self`() {
        val model = ShippingUpdateDataModel(UpdateType.SHIPPED, id, now, otherInfo)
        assertEquals(model, model)
    }

    @Test
    fun `top-level function creates correct model from valid strings`() {
        val typeStr = "shipped"
        val idStr = "abc"
        val ts = now.toEpochMilli().toString()
        val info = "extra"
        val model = ShippingUpdateDataModel(typeStr, idStr, ts, info)
        assertEquals(UpdateType.SHIPPED, model.type)
        assertEquals(idStr, model.id)
        assertEquals(now.toEpochMilli(), model.updateTimestamp.toEpochMilli())
        assertEquals(info, model.otherInfo)
    }

    @Test
    fun `top-level function throws for invalid type string`() {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            ShippingUpdateDataModel("not_a_type", id, now.toEpochMilli().toString(), null)
        }
        assertTrue(ex.message!!.contains("Invalid update type"))
    }

    @Test
    fun `top-level function throws for invalid timestamp string`() {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            ShippingUpdateDataModel("created", id, "not_a_number", null)
        }
        assertTrue(ex.message!!.contains("Invalid timestamp"))
    }
}