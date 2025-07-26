package models.domainmodels

import constants.ShipmentStatus
import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import models.viewmodels.serializers.InstantAsEpochMillisSerializer
import java.time.Instant

@Serializable
data class ShippingUpdateDomainModel(
    val previousStatus: ShipmentStatus,
    val newStatus: ShipmentStatus,
    @Serializable(with = InstantAsEpochMillisSerializer::class)
    val timestamp: Instant
)