<script setup lang="ts">
import { ref, computed } from 'vue'
import * as XLSX from 'xlsx' // Para exportar para Excel
import { jsPDF } from 'jspdf' // Para exportar para PDF

// Dados de exemplo para relatório
const funcionarios = ref([
  { nome: 'Funcionario A', cpf: '123456789', empresa: 'Empresa A', funcao: 'Developer', email: 'a@empresa.com', entrada: '08:00', saida: '17:00', diaTrabalhado: '2025-03-11' },
  { nome: 'Funcionario B', cpf: '987654321', empresa: 'Empresa B', funcao: 'Manager', email: 'b@empresa.com', entrada: '09:00', saida: '18:00', diaTrabalhado: '2025-03-10' },
  { nome: 'Funcionario C', cpf: '192837465', empresa: 'Empresa C', funcao: 'Designer', email: 'c@empresa.com', entrada: '07:00', saida: '16:00', diaTrabalhado: '2025-03-09' },
  // mais funcionários...
])

// Filtro de funcionário
const filtroFuncionario = ref('')

// Computed para filtrar os funcionários
const funcionariosFiltrados = computed(() => {
  return funcionarios.value.filter(f => {
    return f.nome.toLowerCase().includes(filtroFuncionario.value.toLowerCase()) ||
           f.funcao.toLowerCase().includes(filtroFuncionario.value.toLowerCase()) ||
           f.empresa.toLowerCase().includes(filtroFuncionario.value.toLowerCase()) ||
           f.email.toLowerCase().includes(filtroFuncionario.value.toLowerCase())
  })
})

// Função para exportar para Excel
const exportToExcel = () => {
  const ws = XLSX.utils.json_to_sheet(funcionariosFiltrados.value)
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, 'Funcionarios')
  XLSX.writeFile(wb, 'relatorio_funcionarios.xlsx')
}

// Função para exportar para PDF
const exportToPDF = () => {
  const doc = new jsPDF()
  doc.setFontSize(12)

  // Adicionar título
  doc.text('Relatório de Funcionários', 14, 20)

  // Cabeçalho da tabela
  const headers = ['Nome', 'CPF', 'Empresa', 'Função', 'E-mail', 'Entrada', 'Saída', 'Dia Trabalhado']
  let y = 30
  headers.forEach((header, i) => {
    doc.text(header, 14 + i * 30, y)
  })

  // Adicionar os dados da tabela
  funcionariosFiltrados.value.forEach((f, i) => {
    y += 10
    doc.text(f.nome, 14, y)
    doc.text(f.cpf, 44, y)
    doc.text(f.empresa, 74, y)
    doc.text(f.funcao, 104, y)
    doc.text(f.email, 134, y)
    doc.text(f.entrada, 164, y)
    doc.text(f.saida, 194, y)
    doc.text(f.diaTrabalhado, 224, y)
  })

  // Salvar o arquivo PDF
  doc.save('relatorio_funcionarios.pdf')
}
</script>

<template>
  <div class="ml-16 lg:ml-48 p-4 flex flex-col space-y-6">
    <h2 class="text-xl font-bold">Relatório de Funcionários</h2>

    <!-- Campo de Filtro -->
    <div class="mb-4">
      <input
        v-model="filtroFuncionario"
        type="text"
        placeholder="Filtrar por nome, função ou empresa"
        class="p-2 rounded-lg border border-gray-300 shadow-md w-full"
      />
    </div>

    <!-- Tabela de funcionários -->
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

    <!-- Botões de Exportação -->
    <div class="flex space-x-4 mt-6">
      <button @click="exportToExcel" class="px-4 py-2 bg-blue-500 text-white rounded-lg">Exportar para Excel</button>
      <!-- Botão de Exportação para PDF -->
<!-- Botão de Exportação para PDF -->
<button 
  @click="exportToPDF" 
  class="px-4 py-2 bg-[#3ab0ff] text-white rounded-lg shadow-md hover:bg-[#2a94d2] transition-all duration-300"
>
  Exportar para PDF
</button>


    </div>
  </div>
</template>

<style scoped>
/* Estilos personalizados para a tela de relatório */
</style>
