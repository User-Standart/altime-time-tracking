<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <div class="min-h-screen flex flex-col">
    <!-- Só mostra a navbar se NÃO estiver na rota /login -->
    <navbar v-if="route.path !== '/login'" />

    <div class="flex-grow p-5">
      <slot />
    </div>
  </div>
</template>
