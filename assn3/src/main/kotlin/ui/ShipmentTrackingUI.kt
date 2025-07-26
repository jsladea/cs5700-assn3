package ui

import models.viewmodels.ShipmentViewModel
import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.*

class ShipmentTrackingUI : JFrame("Shipment Tracker") {
    private val textField = JTextField(20)
    private val trackButton = JButton("Track")
    private val cardPanel = JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS) }
    private val scrollPane = JScrollPane(cardPanel)
    private val shipmentCards = mutableMapOf<String, JPanel>()
    private val adapter: ShipmentTrackingAdapter

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

        adapter = ShipmentTrackingAdapter { id, viewModel -> updateCardUI(id, viewModel) }

        trackButton.addActionListener(this::onTrack)

        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }

    private fun updateCardUI(id: String, viewModel: ShipmentViewModel) {
        val card = shipmentCards.getOrPut(id) {
            val newCard = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                border = BorderFactory.createTitledBorder("Shipment $id")
            }
            cardPanel.add(newCard)
            cardPanel.revalidate()
            cardPanel.repaint()
            newCard
        }
        card.removeAll()
        card.add(JLabel("Status: ${viewModel.status}"))
        card.add(JLabel("Shipment Type: ${viewModel.shipmentType}"))
        card.add(JLabel("Location: ${viewModel.currentLocation}"))
        card.add(JLabel("Expected Delivery: ${viewModel.expectedDelivery ?: "Currently Unavailable"}"))
        card.add(JLabel("Notes: ${viewModel.notes.plus(viewModel.abnormalEvents).joinToString()}"))
        val removeButton = JButton("Remove Tracking").apply {
            addActionListener { onRemove(id) }
        }
        card.add(removeButton)
        card.revalidate()
        card.repaint()
    }

    private fun onTrack(e: ActionEvent) {
        val id = textField.text.trim()
        if (!shipmentCards.containsKey(id)) {
            try {
                adapter.trackShipment(id)
            } catch (ex: NoSuchElementException) {
                JOptionPane.showMessageDialog(this, "Shipment with ID '$id' not found.")
            } catch (ex: IllegalStateException) {
                JOptionPane.showMessageDialog(this, ex.message)
            }
        }
    }

    private fun onRemove(id: String) {
        adapter.removeShipmentTracking(id)
        shipmentCards.remove(id)?.let { cardPanel.remove(it) }
        cardPanel.revalidate()
        cardPanel.repaint()
    }
}