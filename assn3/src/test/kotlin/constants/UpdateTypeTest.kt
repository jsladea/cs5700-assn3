package constants

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UpdateTypeTest {

    @Test
    fun `fromString returns correct enum for all valid values, case insensitive`() {
        assertEquals(UpdateType.CREATED, UpdateType.fromString("created"))
        assertEquals(UpdateType.CREATED, UpdateType.fromString("CREATED"))
        assertEquals(UpdateType.SHIPPED, UpdateType.fromString("shipped"))
        assertEquals(UpdateType.SHIPPED, UpdateType.fromString("SHIPPED"))
        assertEquals(UpdateType.LOCATION, UpdateType.fromString("location"))
        assertEquals(UpdateType.LOCATION, UpdateType.fromString("LOCATION"))
        assertEquals(UpdateType.DELIVERED, UpdateType.fromString("delivered"))
        assertEquals(UpdateType.DELIVERED, UpdateType.fromString("DELIVERED"))
        assertEquals(UpdateType.DELAYED, UpdateType.fromString("delayed"))
        assertEquals(UpdateType.DELAYED, UpdateType.fromString("DELAYED"))
        assertEquals(UpdateType.LOST, UpdateType.fromString("lost"))
        assertEquals(UpdateType.LOST, UpdateType.fromString("LOST"))
        assertEquals(UpdateType.CANCELED, UpdateType.fromString("canceled"))
        assertEquals(UpdateType.CANCELED, UpdateType.fromString("CANCELED"))
        assertEquals(UpdateType.NOTE_ADDED, UpdateType.fromString("noteadded"))
        assertEquals(UpdateType.NOTE_ADDED, UpdateType.fromString("NOTEADDED"))
    }

    @Test
    fun `fromString throws IllegalArgumentException for invalid value`() {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            UpdateType.fromString("invalid_type")
        }
        assertTrue(ex.message!!.contains("Invalid update type"))
    }

    @Test
    fun `enum values are as expected`() {
        val values = UpdateType.values().map { it.name }
        assertTrue(values.containsAll(listOf(
            "CREATED", "SHIPPED", "LOCATION", "DELIVERED", "DELAYED", "LOST", "CANCELED", "NOTE_ADDED"
        )))
    }
}