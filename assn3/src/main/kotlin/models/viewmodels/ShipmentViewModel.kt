package models.viewmodels

import constants.ShipmentStatus
import models.domainmodels.ShippingUpdateDomainModel
import java.time.Instant

import kotlinx.serialization.Serializable
import models.viewmodels.serializers.InstantAsEpochMillisSerializer

@Serializable
data class ShipmentViewModel(
    val id: String,
    val status: ShipmentStatus,
    val currentLocation: String,
    @Serializable(with = InstantAsEpochMillisSerializer::class)
    val expectedDelivery: Instant?,
    val notes: List<String>,
    val updates: List<ShippingUpdateDomainModel>,
    val abnormalEvents: List<String>,
    val shipmentType: String
)