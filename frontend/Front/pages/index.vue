<script setup>
definePageMeta({
  middleware: 'auth',
})
import { ref, onMounted, watch } from "vue";
import { Chart, registerables } from "chart.js";
import { getFuncionariosPorEmpresa } from "@/services/api"; // ajuste o caminho conforme seu projeto
Chart.register(...registerables);

// Referências dos canvas
const pieChart = ref(null);
const lineChart = ref(null);
const barChart = ref(null);

// Filtros
const empresas = ref([]);
const funcionariosPorEmpresa = ref({});
const empresaSelecionada = ref("");
const funcionarioSelecionado = ref("");
const funcionariosFiltrados = ref([]);

// Atualiza lista de funcionários ao mudar empresa
watch(empresaSelecionada, () => {
  funcionarioSelecionado.value = "";
  funcionariosFiltrados.value = funcionariosPorEmpresa.value[empresaSelecionada.value] || [];
});

// Atualiza gráfico de barras com base no filtro
const atualizarGraficoFiltrado = () => {
  if (!barChart.value) return;
  const chartInstance = Chart.getChart(barChart.value);
  if (chartInstance) chartInstance.destroy();

  const funcionariosData = empresaSelecionada.value
    ? funcionariosPorEmpresa.value[empresaSelecionada.value]
    : [];
  
  const labels = funcionarioSelecionado.value
    ? [funcionarioSelecionado.value]  // Se funcionário for selecionado, mostra só ele
    : funcionariosData;               // Se não, mostra todos os funcionários da empresa

  const data = funcionarioSelecionado.value
    ? [Math.floor(Math.random() * 10)]  // Dado aleatório para o funcionário selecionado
    : funcionariosData.map(() => Math.floor(Math.random() * 10));  // Dados aleatórios para todos os funcionários

  new Chart(barChart.value, {
    type: "bar",
    data: {
      labels: labels,
      datasets: [
        {
          label: "Horas por Profissional",
          data: data,
          backgroundColor: "rgba(54, 162, 235, 0.5)",
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
    },
  });
};

// Função nova: carregar Funcionários por Empresa
const carregarFuncionariosPorEmpresa = async () => {
  try {
    const response = await getFuncionariosPorEmpresa();
    const dados = response.data;

    empresas.value = Object.keys(dados);
    funcionariosPorEmpresa.value = dados;

    if (pieChart.value) {
      const chartInstance = Chart.getChart(pieChart.value);
      if (chartInstance) chartInstance.destroy();
    }

    new Chart(pieChart.value, {
      type: "pie",
      data: {
        labels: empresas.value,
        datasets: [
          {
            label: "Funcionários",
            data: Object.values(dados),
            backgroundColor: [
              "rgba(255, 99, 132, 0.5)",
              "rgba(54, 162, 235, 0.5)",
              "rgba(255, 206, 86, 0.5)",
              "rgba(75, 192, 192, 0.5)",
              "rgba(153, 102, 255, 0.5)",
              "rgba(255, 159, 64, 0.5)"
            ]
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
      }
    });
  } catch (error) {
    console.error("Erro ao carregar funcionários por empresa:", error);
  }
};

onMounted(() => {
  carregarFuncionariosPorEmpresa(); // Chama API para montar o gráfico dinâmico

  // Linha: Profissionais por dia (mantido como estava)
  new Chart(lineChart.value, {
    type: "line",
    data: {
      labels: ["Seg", "Ter", "Qua", "Qui", "Sex"],
      datasets: [
        {
          label: "Profissionais por Dia",
          data: [10, 12, 8, 15, 9],
          fill: false,
          borderColor: "rgba(54, 162, 235, 1)",
          backgroundColor: "rgba(54, 162, 235, 0.5)",
          tension: 0.3,
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
    }
  });
});
</script>
<template>
  <div class="charts-wrapper">
    <div class="chart-container">
      <h2>Funcionários por Empresa</h2>
      <canvas ref="pieChart"></canvas>
    </div>

    <div class="chart-container">
      <h2>Registros de Profissionais por Dia</h2>
      <canvas ref="lineChart"></canvas>
    </div>

    <div class="chart-container">
      <h2>Horas por Profissional</h2>
      <div class="filters">
        <select v-model="empresaSelecionada">
          <option disabled value="">Selecione uma empresa</option>
          <option v-for="empresa in empresas" :key="empresa">{{ empresa }}</option>
        </select>

        <select v-model="funcionarioSelecionado" :disabled="!empresaSelecionada">
          <option disabled value="">Selecione um funcionário</option>
          <option v-for="func in funcionariosFiltrados" :key="func">{{ func }}</option>
        </select>

        <button @click="atualizarGraficoFiltrado">Filtrar</button>
      </div>
      <canvas ref="barChart"></canvas>
    </div>
  </div>
</template>

<style scoped>
.charts-wrapper {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 2rem;
  padding: 2rem;
}

.chart-container {
  width: 40%;
  height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f9f9f9;
  border-radius: 1rem;
  padding: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filters {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

select, button {
  padding: 0.3rem 0.6rem;
  border-radius: 5px;
  border: 1px solid #ccc;
}

button {
  background-color: #3b82f6;
  color: white;
  cursor: pointer;
}
</style>