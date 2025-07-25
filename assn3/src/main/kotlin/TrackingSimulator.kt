import constants.UpdateType
import kotlinx.coroutines.*
import models.datamodels.ShippingUpdateDataModel
import models.domainmodels.ShipmentDomainModel
import updatestrategies.IShipmentUpdateStrategy
import java.io.File

class TrackingSimulator(private val shipments: MutableMap<String, ShipmentDomainModel>, val strategies: Map<UpdateType, IShipmentUpdateStrategy>) {

    fun startSimulation(updateFile: File) {
        CoroutineScope(Dispatchers.Default).launch {
            updateFile.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    System.out.println(line);
                    val update = parseUpdate(line)
                    delay(1000) // process one update per second
                    var shipment = shipments[update.id]
                    if(shipment == null) {
                        shipment = ShipmentDomainModel(update.id, strategies)
                        shipments.put(update.id, shipment)
                    }
                    shipment.update(update)
                }
            }
        }
    }

    private fun parseUpdate(line: String): ShippingUpdateDataModel {
        val parts = line.split(",")
        val type = parts[0]
        val id = parts[1]
        val timestamp = parts[2]
        val otherInfo = if (parts.size > 3) parts.subList(3, parts.size).joinToString(",") else null
        return ShippingUpdateDataModel(type, id, timestamp, otherInfo)
    }
}