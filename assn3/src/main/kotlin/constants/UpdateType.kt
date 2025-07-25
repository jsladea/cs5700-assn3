package constants

enum class UpdateType {
    CREATED,
    SHIPPED,
    LOCATION,
    DELIVERED,
    DELAYED,
    LOST,
    CANCELED,
    NOTE_ADDED;

    companion object {
        fun fromString(value: String): UpdateType {
            return when (value.lowercase()) {
                "created"     -> CREATED
                "shipped"     -> SHIPPED
                "location"    -> LOCATION
                "delivered"   -> DELIVERED
                "delayed"     -> DELAYED
                "lost"        -> LOST
                "canceled"    -> CANCELED
                "noteadded"   -> NOTE_ADDED
                else -> throw IllegalArgumentException("Invalid update type: $value")
            }
        }
    }
}
