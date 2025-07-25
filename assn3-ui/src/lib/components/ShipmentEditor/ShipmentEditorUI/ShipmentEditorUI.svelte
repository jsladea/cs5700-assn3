<script lang="ts">
  import Dropdown from '../../Dropdown/Dropdown.svelte';
  import TextBox from '../../TextBox/TextBox.svelte';
  import Button from '../../Button/Button.svelte';

  const uid = $props.id();
  let { id=uid, updateTypes, updateType, onUpdateTypeSelect, shipmentId = $bindable(), timestamp = $bindable(), otherInfoLabel, otherInfoPlaceHolder="", otherInfo = $bindable(), onSubmitClick, submitDisabled=false } = $props<{
    id?: string;
    updateTypes: string[];
    updateType: string;
    onUpdateTypeSelect: (value: string) => void;
    shipmentId: string;
    timestamp: string;
    otherInfoLabel: string | null | undefined;
    otherInfo: string;
    otherInfoPlaceHolder?: string;
    onSubmitClick: () => void;
    submitDisabled?: boolean;
  }>();
</script>

<div 
  id={id}
  class="shipment-editor-ui"
>
  <Dropdown
    id="{id}-update-type-dropdown"
    label="Update Type:"
    options={updateTypes}
    selected={updateType}
    onSelect={onUpdateTypeSelect}
  />

  <TextBox
    id="{id}-shipment-id-textbox"
    label="Shipment Id:"
    placeholder="Enter Shipment ID"
    bind:value={shipmentId}
  />

  <TextBox
    id="{id}-timestamp-textbox"
    label="Timestamp:"
    placeholder="Enter Timestamp"
    bind:value={timestamp}
  />

  {#if otherInfoLabel}
    <TextBox
      id="{id}-other-info-textbox"
      label={otherInfoLabel}
      placeholder={otherInfoPlaceHolder}
      bind:value={otherInfo}
    />
  {/if}

  <Button
    id="{id}-submit-button"
    onClick={onSubmitClick}
    disabled={submitDisabled}
  >
    Submit
  </Button>
</div>

<style>
  .shipment-editor-ui {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    max-width: 400px;
    margin: auto;
    padding: 1rem;
    border: 1px solid #ccc;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    background-color: #fff;
  }
</style>