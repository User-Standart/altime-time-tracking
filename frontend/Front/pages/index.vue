<template>
  <div class="flex flex-wrap gap-6 justify-center p-6">
    <!-- Gráfico de Funcionários por Empresa -->
    <UCard class="w-full md:w-[500px]">
      <template #header>
        <h2 class="text-lg font-medium text-center">Funcionários por Empresa</h2>
      </template>

      <div class="h-[300px]">
        <canvas ref="pieChart"></canvas>
      </div>
    </UCard>

    <!-- Gráfico de Horas por Profissional -->
    <UCard class="w-full md:w-[600px]">
      <template #header>
        <h2 class="text-lg font-medium text-center">Horas por Profissional</h2>
      </template>

      <!-- Filtros -->
      <div class="flex flex-wrap items-center gap-4">
        <USelect
          v-model="empresaSelecionada"
          :options="empresas"
          placeholder="Selecione uma empresa"
          class="flex-1"
        />
        <USelect
          v-model="funcionarioSelecionado"
          :options="funcionariosFiltrados"
          placeholder="Selecione um funcionário"
          :disabled="!empresaSelecionada"
          class="flex-1"
        />
        <UButton color="blue" @click="atualizarGraficoFiltrado">
          Filtrar
        </UButton>
      </div>

      <div v-if="empresaSelecionada && funcionariosFiltrados.length === 0" class="text-center text-red-500 mt-4">
        Nenhum funcionário encontrado para esta empresa.
      </div>

      <div v-else class="h-[300px] mt-4">
        <canvas ref="barChart"></canvas>
      </div>
    </UCard>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { Chart, registerables } from 'chart.js'
import { getFuncionariosPorEmpresa } from '@/services/api'

Chart.register(...registerables)

const pieChart = ref<HTMLCanvasElement | null>(null)
const barChart = ref<HTMLCanvasElement | null>(null)

const empresas = ref<string[]>([])
const funcionariosPorEmpresa = ref<Record<string, string[]>>({})
const empresaSelecionada = ref('')
const funcionarioSelecionado = ref('')
const funcionariosFiltrados = ref<string[]>([])

watch(empresaSelecionada, () => {
  funcionarioSelecionado.value = ''
  funcionariosFiltrados.value = funcionariosPorEmpresa.value[empresaSelecionada.value] || []
})

// Gráfico de Barras
const atualizarGraficoFiltrado = () => {
  if (!barChart.value) return
  const chartInstance = Chart.getChart(barChart.value)
  if (chartInstance) chartInstance.destroy()

  const funcionariosData = empresaSelecionada.value
    ? funcionariosPorEmpresa.value[empresaSelecionada.value]
    : []

  if (!funcionariosData || funcionariosData.length === 0) {
    return
  }

  const labels = funcionarioSelecionado.value
    ? [funcionarioSelecionado.value]
    : funcionariosData

  const data = labels.map(() => Math.floor(Math.random() * 8 + 1)) // Simula 1 a 8 horas

  new Chart(barChart.value, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [
        {
          label: 'Horas por Profissional',
          data: data,
          backgroundColor: 'rgba(37, 99, 235, 0.8)',
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false,
        },
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            stepSize: 1,
          },
        },
      },
    },
  })
}

// Gráfico de Pizza
const carregarFuncionariosPorEmpresa = async () => {
  try {
    const response = await getFuncionariosPorEmpresa()
    const dados = response.data

    empresas.value = Object.keys(dados)
    funcionariosPorEmpresa.value = dados

    if (pieChart.value) {
      const chartInstance = Chart.getChart(pieChart.value)
      if (chartInstance) chartInstance.destroy()
    }

    new Chart(pieChart.value, {
      type: 'pie',
      data: {
        labels: empresas.value,
        datasets: [
          {
            label: 'Funcionários',
            data: Object.values(dados).map(item => item.length || 0),
            backgroundColor: [
              '#ef4444',
              '#3b82f6',
              '#10b981',
              '#f59e0b',
              '#8b5cf6',
              '#ec4899',
            ],
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
      },
    })
  } catch (error) {
    console.error('Erro ao carregar funcionários por empresa:', error)
  }
}

onMounted(() => {
  carregarFuncionariosPorEmpresa()
})
</script>
