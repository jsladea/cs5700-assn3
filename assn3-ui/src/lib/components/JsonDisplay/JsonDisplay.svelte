<script lang="ts">

  // Accept a prop called 'json'
  const { json } = $props<{ json: string }>();

  // Parse and pretty-print the JSON, or show error if invalid
  const prettyJson = $derived(() => {
    try {
      return JSON.stringify(JSON.parse(json), null, 2);
    } catch (e) {
      return 'Invalid JSON';
    }
  });
</script>

<style>
  .json-container {
    border: 1.5px solid #d1d5db;
    border-radius: 0.75rem;
    background: #f9fafb;
    padding: 1.25rem;
    font-family: 'Fira Mono', 'Consolas', monospace;
    font-size: 1rem;
    color: #374151;
    overflow-x: auto;
    box-shadow: 0 2px 8px 0 rgba(0,0,0,0.03);
    margin: 0.5rem 0;
    transition: border-color 0.2s;
  }
  .json-container:focus-within {
    border-color: #2563eb;
  }
  pre {
    margin: 0;
    white-space: pre-wrap;
    word-break: break-word;
  }
  .error {
    color: #dc2626;
    font-weight: bold;
  }
</style>

<div class="json-container">
  {#if prettyJson() === 'Invalid JSON'}
    <pre class="error">{prettyJson()}</pre>
  {:else}
    <pre>{prettyJson()}</pre>
  {/if}
</div>