package ui

import TrackingSimulator
import constants.UpdateType
import models.domainmodels.ShipmentDomainModel
import models.interfaces.IObserver
import updatestrategies.CanceledStrategy
import updatestrategies.CreatedStrategy
import updatestrategies.DelayedStrategy
import updatestrategies.DeliveredStrategy
import updatestrategies.IShipmentUpdateStrategy
import updatestrategies.LocationStrategy
import updatestrategies.LostStrategy
import updatestrategies.NoteAddedStrategy
import updatestrategies.ShippedStrategy
import java.awt.EventQueue.invokeLater
import java.awt.event.ActionEvent
import java.io.File
import java.awt.*
import javax.swing.*
import javax.swing.SwingUtilities.invokeLater

class ShipmentTrackingUI(private val shipments: Map<String, ShipmentDomainModel>) : JFrame("Shipment Tracker") {
    private val textField = JTextField(20)
    private val trackButton = JButton("Track")
    private val cardPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }
    private val scrollPane = JScrollPane(cardPanel)
    private val shipmentCards = mutableMapOf<String, JPanel>()

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()
        preferredSize = Dimension(800, 600)

        val inputPanel = JPanel().apply {
            layout = FlowLayout()
            add(JLabel("Shipment ID: "))
            add(textField)
            add(trackButton)
        }

        add(inputPanel, BorderLayout.NORTH)
        add(scrollPane, BorderLayout.CENTER)

        trackButton.addActionListener(this::onTrack)

        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }

    private fun updateCardUI(shipment: ShipmentDomainModel, card: JPanel, removeButton: JButton) {
        card.removeAll()

        card.add(JLabel("Status: ${shipment.status}"))
        card.add(JLabel("Location: ${shipment.currentLocation}"))
        card.add(JLabel("Expected Delivery: ${shipment.expectedDelivery ?: "Currently Unavailable"}"))
        card.add(JLabel("Notes: ${shipment.notes.joinToString()}"))
        card.add(removeButton)
        card.revalidate()
        card.repaint()
    }


    private fun onTrack(e: ActionEvent) {
        val id = textField.text.trim()
        val shipment = shipments[id]
        if (shipment == null) {
            JOptionPane.showMessageDialog(this, "Shipment with ID '$id' not found.")
        } else {
            if (!shipmentCards.containsKey(id)) {
                val card = JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    border = BorderFactory.createTitledBorder("Shipment $id")
                }

                lateinit var observer: IObserver<ShipmentDomainModel>

                val removeButton = JButton("Remove Tracking").apply {
                    addActionListener {
                        shipmentCards.remove(id)?.let { cardPanel.remove(it) }
                        shipment.removeObserver(observer)
                        cardPanel.revalidate()
                        cardPanel.repaint()
                    }
                }


                cardPanel.add(card)
                shipmentCards[id] = card
                cardPanel.revalidate()
                cardPanel.repaint()

                observer = object : IObserver<ShipmentDomainModel> {
                    override fun update(subject: ShipmentDomainModel) {
                        updateCardUI(shipment, card, removeButton)
                    }
                }

                shipment.addObserver(observer)
                updateCardUI(shipment, card, removeButton)
            }
        }
    }

    companion object {

        private val strategyMap = mutableMapOf<UpdateType, IShipmentUpdateStrategy>().apply {
            put(UpdateType.CREATED, CreatedStrategy())
            put(UpdateType.SHIPPED, ShippedStrategy())
            put(UpdateType.LOCATION, LocationStrategy())
            put(UpdateType.DELIVERED, DeliveredStrategy())
            put(UpdateType.DELAYED, DelayedStrategy())
            put(UpdateType.LOST, LostStrategy())
            put(UpdateType.CANCELED, CanceledStrategy())
            put(UpdateType.NOTE_ADDED, NoteAddedStrategy())
        }

        fun launchWithSimulation(filePath: String) {
            val shipments = mutableMapOf<String, ShipmentDomainModel>()
            val file = File(filePath)

            val simulator = TrackingSimulator(shipments, strategyMap)
            simulator.startSimulation(file)

            SwingUtilities.invokeLater {
                ShipmentTrackingUI(shipments)
            }
        }
    }
}