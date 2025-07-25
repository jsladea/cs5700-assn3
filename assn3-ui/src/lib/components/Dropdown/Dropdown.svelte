<script lang="ts">
  const uid = $props.id();
  let { id=uid, options = [], selected = "", onSelect, disabled = false, customStyles={}, label="" } = $props();

  let isOpen = $state(false);

  const toggleDropdown = () => {
    if (!disabled) {
      isOpen = !isOpen;
    }
  };

  const selectOption = (option: string) => {
    onSelect(option);
    isOpen = false;
  };

  let styleString = $derived(
    Object.entries(customStyles)
      .map(([key, value]) => `${key}: ${value};`)
      .join(' ')
  );
</script>

<div 
  id={id}
  class="dropdown-container"
  style={styleString}
>
  <label for="{id}-dropdown-button">{label}</label>
  <button
    id="{id}-dropdown-button"
    class="dropdown-button {disabled ? ' disabled' : ''}"
    onclick={toggleDropdown}
    disabled={disabled}
  >
    {options.includes(selected) ? selected : "Select an option"}
    <span class="dropdown-arrow">▼</span>
  </button>
  {#if isOpen}
    <div class="dropdown-menu">
      {#each options as option}
        <div
          class="dropdown-menu-item"
          onclick={() => selectOption(option)}
        >
          {option}
        </div>
      {/each}
    </div>
  {/if}
</div>

<style>
  .dropdown-container {
    position: relative;
    width: 100%;
  }

  .dropdown-button {
    width: 100%;
    padding: 0.75rem;
    font-size: 1rem;
    border: 1px solid #ccc;
    border-radius: 0.5rem;
    background-color: #fff;
    cursor: pointer;
    transition: border-color 0.3s, box-shadow 0.3s;
    outline: none;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .dropdown-button:focus {
    border-color: #007bff;
    box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
  }

  .dropdown-button.disabled {
    background-color: #f8f9fa;
    cursor: not-allowed;
    opacity: 0.6;
  }

  .dropdown-arrow {
    margin-left: 0.5rem;
    font-size: 1rem;
    color: #888;
    pointer-events: none;
  }

  .dropdown-menu {
    position: absolute;
    top: 100%;
    left: 0;
    width: 100%;
    margin-top: 0.5rem;
    border: 1px solid #ccc;
    border-radius: 0.5rem;
    background-color: #fff;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    z-index: 1000;
    overflow: hidden;
  }

  .dropdown-menu-item {
    padding: 0.75rem;
    font-size: 1rem;
    cursor: pointer;
    transition: background-color 0.3s;
  }

  .dropdown-menu-item:hover {
    background-color: #f1f1f1;
  }

  .dropdown-menu-item.disabled {
    cursor: not-allowed;
    opacity: 0.6;
  }
</style>