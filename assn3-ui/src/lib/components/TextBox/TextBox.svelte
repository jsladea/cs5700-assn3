<script lang="ts">
  const uid = $props.id();
  let { id=uid, placeholder = "Enter text...", value = $bindable(""), disabled = false, customStyles={}, label="" } = $props();

  let styleString = $derived(
    Object.entries(customStyles)
      .map(([key, value]) => `${key}: ${value};`)
      .join(' ')
  );

</script>

<label for={id}>{label}</label>
<input
  id={id}
  type="text"
  class="textbox {disabled ? ' disabled' : ''}"
  placeholder={placeholder}
  bind:value={value}
  disabled={disabled}
  style={styleString}
/>

<style>
  label {
    width: 100%;
  }

  .textbox {
    width: 100%;
    padding: 0.75rem;
    font-size: 1rem;
    border: 1px solid #ccc;
    border-radius: 0.5rem;
    transition: border-color 0.3s, box-shadow 0.3s;
    outline: none;
    box-sizing: border-box;
  }

  .textbox:focus {
    border-color: #007bff;
    box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
  }

  .textbox.disabled {
    background-color: #f8f9fa;
    cursor: not-allowed;
    opacity: 0.6;
  }

</style>