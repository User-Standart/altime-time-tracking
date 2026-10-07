<script setup lang="ts">
import routes from '../utils/routes'
import { useColorMode } from '#imports'
import { ref, computed } from 'vue'

const colorMode = useColorMode()
const isHovered = ref(false)

// Define a cor do texto dinamicamente
const textColor = 'text-black'

// Define a cor da linha ao passar o mouse
const hoverLineColor = 'bg-black'

// Função de logout
function logout() {
  localStorage.removeItem('authToken')
  navigateTo('/login')
}
</script>

<template>
  <aside 
    class="fixed top-0 left-0 h-screen w-16 flex flex-col items-center justify-between p-4 rounded-r-2xl transition-all duration-300 shadow-lg border-r-2 border-gray-300 bg-blue-200"
    :class="{ 'w-48': isHovered }"
    @mouseenter="isHovered = true" 
    @mouseleave="isHovered = false"
  >
    <img 
      src="/logo.svg"
      alt="Logo" 
      class="h-12 transition-all duration-300" 
      :class="{ 'h-16': isHovered }"
    />

    <nav class="mt-4 w-full">
      <ul class="space-y-4">
        <li v-for="item in routes" :key="item.to">
          <NuxtLink 
            :to="item.to" 
            class="flex items-center p-2 rounded-lg transition-all duration-300 relative group"
            :class="[textColor, 'hover:bg-blue-300']"
          >
            <!-- Ícone com tamanho dinâmico -->
            <UIcon :name="item.icon" class="transition-all duration-300" 
              :class="isHovered ? 'w-10 h-12' : 'w-10 h-12'" />
            
            <!-- Texto do menu (só aparece quando expandido) -->
            <span v-if="isHovered" class="ml-3 transition-all duration-300">{{ item.label }}</span>

            <!-- Linha de destaque ao passar o mouse -->
            <span 
              class="absolute bottom-0 left-0 w-0 h-[2px] transition-all duration-300 group-hover:w-full"
              :class="[hoverLineColor]"
            ></span>
          </NuxtLink>
        </li>
      </ul>
    </nav>

    <!-- Parte inferior (botão de tema + logout) -->
    <div class="flex flex-col items-center space-y-2 mt-6">
      <buttonTheme />
      <button 
        @click="logout"
        class="flex items-center p-2 rounded-lg transition-all duration-300 text-black hover:bg-blue-300"
      >
        <span class="text-xl mr-2">⏻</span>
        <span v-if="isHovered" class="transition-all duration-300">Sair</span>
      </button>
    </div>

  </aside>
</template>
