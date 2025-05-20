<template>
    <div v-if="pending" class="loading-container">
      Carregando...
    </div>
    <div v-else class="layout-container">
      <slot />
    </div>
  </template>
  
  <script setup lang="ts">
  import { useRouter } from 'vue-router'
  import { ref, onMounted } from 'vue'
  
  const pending = ref(true)
  
  onMounted(() => {
    const token = localStorage.getItem('authToken')
    if (!token && window.location.pathname !== '/login') {
      window.location.href = '/login'
    }
    pending.value = false
  })
  </script>
  
  <style scoped>
  .layout-container {
    min-height: 100vh;
    background: white;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .loading-container {
    min-height: 100vh;
    background: white;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 24px;
    font-weight: bold;
  }
  </style>
  