package models.viewmodels

import constants.UpdateType
import models.datamodels.ShippingUpdateDataModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.Instant

@Serializable
data class ShippingUpdateViewModel(
    @SerialName("updateType")
    val updateType: String,
    @SerialName("id")
    val id: String,
    @SerialName("timestamp")
    val timestamp: String,
    @SerialName("otherInfo")
    val otherInfo: String? = null
) {
    fun toDataModel(): ShippingUpdateDataModel {
        val type = UpdateType.fromString(updateType)
        val instant = try {
            Instant.ofEpochMilli(timestamp.toLong())
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid timestamp: $timestamp", e)
        }
        return ShippingUpdateDataModel(type, id, instant, otherInfo)
    }
}