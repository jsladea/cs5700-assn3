package models.domainmodels

import constants.ShipmentStatus
import java.time.Instant

data class ShippingUpdateDomainModel(
    val previousStatus: ShipmentStatus,
    val newStatus: ShipmentStatus,
    val timestamp: Instant
)