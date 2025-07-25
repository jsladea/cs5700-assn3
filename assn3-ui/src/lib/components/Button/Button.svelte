<script lang="ts">

  const uid = $props.id();
  let { id=uid, type = "button", disabled = false, onClick = null, customStyles={} } = $props();

  let styleString = $derived(
    Object.entries(customStyles)
      .map(([key, value]) => `${key}: ${value};`)
      .join(' ')
  );

</script>

<button
  id={id}
  type={type}
  disabled={disabled}
  onclick={(event: Event) => onClick?.(event)}
  style={styleString}
>
  <slot/>
</button>

<style>
  button {
    padding: 0.5rem 1rem;
    color : white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    background-color: #2563eb;
  }

  button:hover {
    background-color: #60a5fa;
  }

  button:hover, button:focus {
    outline: none;
    box-shadow: 0 0 0 3px var(--focus-ring-color, rgba(0, 123, 255, 0.25));
  }

  button:disabled {
    background-color: #d1d5db;
    cursor: not-allowed;
    opacity: 0.6;
    box-shadow: none;
  }
</style>