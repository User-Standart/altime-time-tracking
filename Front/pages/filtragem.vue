<script setup lang="ts">
import routes from '../utils/routes'
import { useColorMode } from '#imports'
import { ref, computed } from 'vue'

const colorMode = useColorMode()
const isHovered = ref(false)

// Define a cor do texto dinamicamente
const textColor = computed(() => (colorMode.value === 'dark' ? 'text-white' : 'text-black'))

// Define a cor da linha ao passar o mouse
const hoverLineColor = computed(() => (colorMode.value === 'dark' ? 'bg-white' : 'bg-black'))

// Definindo os dados de exemplo para funcionários
const filtragem = ref('')  // Barra de pesquisa única

const funcionarios = ref([
  { nome: 'Funcionario A', cpf: '123456789', empresa: 'Empresa A', funcao: 'Developer', email: 'a@empresa.com', entrada: '08:00', saida: '17:00', diaTrabalhado: '2025-03-11' },
  { nome: 'Funcionario B', cpf: '987654321', empresa: 'Empresa B', funcao: 'Manager', email: 'b@empresa.com', entrada: '09:00', saida: '18:00', diaTrabalhado: '2025-03-10' },
  { nome: 'Funcionario C', cpf: '192837465', empresa: 'Empresa C', funcao: 'Designer', email: 'c@empresa.com', entrada: '07:00', saida: '16:00', diaTrabalhado: '2025-03-09' },
  // mais funcionários...
])

// Computed para filtrar os dados
const funcionariosFiltrados = computed(() => {
  return funcionarios.value.filter(f => {
    const pesquisa = filtragem.value.toLowerCase()
    return (
      f.nome.toLowerCase().includes(pesquisa) ||
      f.cpf.includes(pesquisa) ||
      f.empresa.toLowerCase().includes(pesquisa) ||
      f.funcao.toLowerCase().includes(pesquisa) ||
      f.email.toLowerCase().includes(pesquisa) ||
      f.entrada.includes(pesquisa) ||
      f.saida.includes(pesquisa) ||
      f.diaTrabalhado.includes(pesquisa)
    )
  })
})
</script>

<template>
  <aside 
    class="fixed top-0 left-0 h-screen w-16 flex flex-col items-center justify-between p-4 rounded-r-2xl transition-all duration-300 shadow-lg border-r-2 border-gray-300 bg-blue-200"
    :class="{ 'w-48': isHovered }"
    @mouseenter="isHovered = true" 
    @mouseleave="isHovered = false"
  >
    <img 
      :src="colorMode.value === 'light' ? '/logo.svg' : '/logo-white.svg'" 
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
            <!-- Ícone sempre visível -->
            <UIcon :name="item.icon" class="w-6 h-6" />
            
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
    
    <buttonTheme />
  </aside>

  <!-- Conteúdo principal -->
  <div class="ml-16 lg:ml-48 p-4 flex flex-col space-y-6">
    <h2 class="text-xl font-bold">Filtragem de Dados</h2>

    <!-- Barra de pesquisa -->
    <div class="space-y-4">
      <input 
        v-model="filtragem" 
        placeholder="Pesquisar por nome, CPF, empresa, função, e-mail, entrada, saída ou dia trabalhado" 
        class="p-2 rounded-lg border border-gray-300 shadow-md w-full" 
      />
    </div>

    <!-- Exibe os filtros aplicados -->
    <div class="mt-4">
      <h3 class="text-lg font-semibold">Resultado da pesquisa:</h3>
      <p><strong>Busca:</strong> {{ filtragem }}</p>
    </div>

    <!-- Tabela de resultados -->
    <div class="overflow-x-auto bg-white rounded-lg shadow-md">
      <table class="min-w-full table-auto border-collapse">
        <thead class="bg-gray-200">
          <tr>
            <th class="border px-4 py-2 text-left">Nome</th>
            <th class="border px-4 py-2 text-left">CPF</th>
            <th class="border px-4 py-2 text-left">Empresa</th>
            <th class="border px-4 py-2 text-left">Função</th>
            <th class="border px-4 py-2 text-left">E-mail</th>
            <th class="border px-4 py-2 text-left">Entrada</th>
            <th class="border px-4 py-2 text-left">Saída</th>
            <th class="border px-4 py-2 text-left">Dia Trabalhado</th>
          </tr>
        </thead>
        <tbody>
          <!-- Exibindo os funcionários filtrados -->
          <tr v-for="f in funcionariosFiltrados" :key="f.cpf">
            <td class="border px-4 py-2">{{ f.nome }}</td>
            <td class="border px-4 py-2">{{ f.cpf }}</td>
            <td class="border px-4 py-2">{{ f.empresa }}</td>
            <td class="border px-4 py-2">{{ f.funcao }}</td>
            <td class="border px-4 py-2">{{ f.email }}</td>
            <td class="border px-4 py-2">{{ f.entrada }}</td>
            <td class="border px-4 py-2">{{ f.saida }}</td>
            <td class="border px-4 py-2">{{ f.diaTrabalhado }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
/* Estilos personalizados para a sidebar */
</style>
