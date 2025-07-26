<script lang="ts">
  import JsonDisplay from './lib/components/JsonDisplay/JsonDisplay.svelte';
  import ShipmentEditor from './lib/components/ShipmentEditor/ShipmentEditor.svelte';

  let jsonText = $state("");

  const handleRequest = (response: string) => {
    jsonText = response;
  };
  
</script>

<h1>CS5700</h1>
<h2>Assignment 3</h2>
<h3>Shipping Web UI</h3>
<div
  class="content"
>
  <ShipmentEditor
    id="shipment-editor"
    onRequest={handleRequest}
  >
  </ShipmentEditor>
  {#if jsonText}
    <h3>Response</h3>
    <JsonDisplay
      json={jsonText}
    ></JsonDisplay>
  {/if}
</div>

<style>
  .content {
    display: flex;
    align-items: center;
    flex-direction: column;
  }
</style>
