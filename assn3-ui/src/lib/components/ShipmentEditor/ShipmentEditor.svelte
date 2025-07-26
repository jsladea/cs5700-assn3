<script lang="ts">
  import { createShipment, updateShipment } from '../../utils/ShipmentApi';
  import { convertStringToUpdateType } from '../../utils/UpdateTypeUtils';
  import ShipmentEditorUI from './ShipmentEditorUI/ShipmentEditorUI.svelte';

  const uid = $props.id();
  let { id=uid, onRequest } = $props<{id?: string; onRequest: (response: string) => void}>();

  const updateTypes = [
    "CREATED",
    "SHIPPED",
    "DELIVERED",
    "DELAYED",
    "LOST",
    "CANCELED",
    "LOCATION CHANGED",
    "ADD NOTE"
  ];

  let updateType: string = $state("");
  let shipmentId: string = $state("");
  let timestamp: string = $state("");
  let otherInfo: string = $state("");

  const showOtherInfoTypes = [
    "SHIPPED",
    "LOCATION CHANGED",
    "DELAYED",
    "ADD NOTE",
    "CREATED"
  ];

  let otherInfoLabel = $derived((() => {
    switch (updateType) {
      case "SHIPPED":
      case "DELAYED":
        return "Expected Delivery Timestamp:";
      case "LOCATION CHANGED":
        return "Location:";
      case "ADD NOTE":
        return "Note:";
      case "CREATED":
        return "Shipment Type:";
      default:
        return undefined;
    }
  })());

  let otherInfoPlaceHolder = $derived((() => {
    switch (updateType) {
      case "SHIPPED":
      case "DELAYED":
        return "Enter expected delivery timestamp";
      case "LOCATION CHANGED":
        return "Enter new location";
      case "ADD NOTE":
        return "Enter note";
      case "CREATED":
        return "standard, express, overnight, bulk";
      default:
        return "";
    }
  })());

  let allFieldsComplete = $derived(shipmentId.trim() && timestamp.trim() && (showOtherInfoTypes.includes(updateType) ? otherInfo.trim() : true));

  function handleUpdateTypeSelect(value: string) {
    updateType = value;
    otherInfo = "";
    timestamp = "";
  }

  const handleSubmitClick = async () => {
    const shipment = {
      id: shipmentId,
      timestamp: timestamp,
      otherInfo: otherInfo || undefined,
      updateType: convertStringToUpdateType(updateType)
    };

    let result = undefined;
    try {
      if(updateType === "CREATED") {
        result = await createShipment(shipment);
      } else {
        result = await updateShipment(shipment);
      }
      onRequest(JSON.stringify(result, null, 2));
    }
    catch (error) {
      console.error("Error submitting shipment:", error);
      onRequest(JSON.stringify(error, null, 2));
    }
    
  }
</script>

<ShipmentEditorUI
  id="{id}-ui"
  updateTypes={updateTypes}
  updateType={updateType}
  onUpdateTypeSelect={handleUpdateTypeSelect}
  bind:shipmentId
  bind:timestamp
  otherInfoLabel={otherInfoLabel}
  otherInfoPlaceHolder={otherInfoPlaceHolder}
  bind:otherInfo
  onSubmitClick={handleSubmitClick}
  submitDisabled={!allFieldsComplete}
/>