package updatestrategies

import constants.UpdateType
import models.datamodels.ShippingUpdateDataModel
import models.domainmodels.StandardShipment
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class NoteAddedStrategyTest {
    private lateinit var strategy: NoteAddedStrategy
    private lateinit var shipment: StandardShipment
    private val now = Instant.now()

    @BeforeEach
    fun setup() {
        strategy = NoteAddedStrategy()
        shipment = StandardShipment("id", emptyMap(), now)
    }

    @Test
    fun `apply adds note if otherInfo is present`() {
        val update = ShippingUpdateDataModel(UpdateType.NOTE_ADDED, "id", now, "A note")
        strategy.apply(shipment, update)
        assertEquals(listOf("A note"), shipment.notes)
    }

    @Test
    fun `apply does nothing if otherInfo is null`() {
        val update = ShippingUpdateDataModel(UpdateType.NOTE_ADDED, "id", now, null)
        strategy.apply(shipment, update)
        assertTrue(shipment.notes.isEmpty())
    }
}