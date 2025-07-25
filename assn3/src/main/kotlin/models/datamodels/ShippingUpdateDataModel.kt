package models.datamodels

import constants.UpdateType
import java.time.Instant

fun ShippingUpdateDataModel(
    typeStr: String,
    id: String,
    updateTimestampStr: String,
    otherInfo: String? = null
): ShippingUpdateDataModel {
    val type = UpdateType.fromString(typeStr)
    val timestamp = try {
        Instant.ofEpochMilli(updateTimestampStr.toLong())
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException("Invalid timestamp: $updateTimestampStr", e)
    }

    return ShippingUpdateDataModel(type, id, timestamp, otherInfo)
}


data class ShippingUpdateDataModel(
    val type: UpdateType,
    val id: String,
    val updateTimestamp: Instant,
    val otherInfo: String? = null
)
