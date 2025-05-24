<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import {
  UCard, UButton, UInput, UFormGroup, UModal, UPagination, URadioGroup, UPopover
} from '#components'

interface RegistroPonto {
  id: number
  empresa: string
  funcionario: string
  diaTrabalhado: string
  horarioEntrada: string
  horarioSaida: string
  observacoes: string
}

const paginaAtual = ref(1)
const pageSize = 5

const registros = ref<RegistroPonto[]>([
  {
    id: 1,
    empresa: 'Empresa A',
    funcionario: 'João',
    diaTrabalhado: '2025-05-15',
    horarioEntrada: '08:00',
    horarioSaida: '17:00',
    observacoes: 'Nenhuma'
  },
  {
    id: 2,
    empresa: 'Empresa B',
    funcionario: 'Maria',
    diaTrabalhado: '2025-05-14',
    horarioEntrada: '09:00',
    horarioSaida: '18:00',
    observacoes: 'Atraso justificado'
  }
])

const termoPesquisa = ref('')
const filtroSelecionado = ref('empresa')
const filtroOptions = [
  { value: 'empresa', label: 'Empresa' },
  { value: 'funcionario', label: 'Funcionário' },
  { value: 'diaTrabalhado', label: 'Dia Trabalhado' }
]

const registrosFiltrados = computed(() => {
  const busca = termoPesquisa.value.toLowerCase()
  return registros.value.filter(reg =>
    reg[filtroSelecionado.value as keyof RegistroPonto]
      ?.toString().toLowerCase().includes(busca)
  )
})

const registrosPaginados = computed(() => {
  const start = (paginaAtual.value - 1) * pageSize
  return registrosFiltrados.value.slice(start, start + pageSize)
})

// ESTADO DOS CHECKBOXES (IDs selecionados)
const selecionados = ref<Set<number>>(new Set())

// Função para alternar seleção ao clicar no checkbox
function toggleSelecionado(id: number) {
  if (selecionados.value.has(id)) {
    selecionados.value.delete(id)
  } else {
    selecionados.value.add(id)
  }
  // Para reatividade:
  selecionados.value = new Set(selecionados.value)
}

// Função para selecionar/deselecionar todos visíveis na página
function toggleSelecionarTodos(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.checked) {
    registrosPaginados.value.forEach(r => selecionados.value.add(r.id))
  } else {
    registrosPaginados.value.forEach(r => selecionados.value.delete(r.id))
  }
  selecionados.value = new Set(selecionados.value)
}

function todosSelecionadosNaPagina() {
  return registrosPaginados.value.every(r => selecionados.value.has(r.id)) && registrosPaginados.value.length > 0
}

// MODAL DE CADASTRO/EDIÇÃO
const modalAberto = ref(false)
const registroEditando = reactive<Partial<RegistroPonto>>({})

function abrirModalParaCadastro() {
  Object.assign(registroEditando, {
    id: undefined,
    empresa: '',
    funcionario: '',
    diaTrabalhado: '',
    horarioEntrada: '',
    horarioSaida: '',
    observacoes: ''
  })
  modalAberto.value = true
}

function abrirModalParaEditar(registro: RegistroPonto) {
  Object.assign(registroEditando, { ...registro })
  modalAberto.value = true
}

function fecharModal() {
  modalAberto.value = false
}

function salvarCadastro() {
  if (
    !registroEditando.empresa ||
    !registroEditando.funcionario ||
    !registroEditando.diaTrabalhado ||
    !registroEditando.horarioEntrada ||
    !registroEditando.horarioSaida
  ) {
    alert('Preencha todos os campos obrigatórios')
    return
  }

  if (registroEditando.id) {
    const idx = registros.value.findIndex(r => r.id === registroEditando.id)
    if (idx >= 0) registros.value[idx] = { ...(registroEditando as RegistroPonto) }
  } else {
    registros.value.push({
      ...(registroEditando as RegistroPonto),
      id: Math.max(0, ...registros.value.map(r => r.id)) + 1
    })
  }

  fecharModal()
}

// MODAL DE EXCLUSÃO
const confirmarExclusaoAberto = ref(false)
const registroParaExcluir = ref<RegistroPonto | null>(null)

function abrirConfirmarExclusao(registro: RegistroPonto) {
  registroParaExcluir.value = registro
  confirmarExclusaoAberto.value = true
}

function cancelarExclusao() {
  confirmarExclusaoAberto.value = false
  registroParaExcluir.value = null
}

function confirmarExclusao() {
  if (registroParaExcluir.value) {
    registros.value = registros.value.filter(r => r.id !== registroParaExcluir.value!.id)
    cancelarExclusao()
  }
}

// FUNÇÕES DE EXPORTAÇÃO

function exportarCSV() {
  // Se tiver selecionados, exporta só eles. Senão, exporta todos filtrados.
  const paraExportar = selecionados.value.size > 0
    ? registros.value.filter(r => selecionados.value.has(r.id))
    : registrosFiltrados.value

  const headers = ['Empresa', 'Funcionário', 'Dia Trabalhado', 'Horário Entrada', 'Horário Saída', 'Observações']
  const rows = paraExportar.map(reg => [
    reg.empresa, reg.funcionario, reg.diaTrabalhado,
    reg.horarioEntrada, reg.horarioSaida, reg.observacoes
  ])
  const csvContent = [headers, ...rows]
    .map(e => e.map(v => `"${v}"`).join(','))
    .join('\n')

  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.setAttribute('href', url)
  link.setAttribute('download', 'registros.csv')
  link.click()
}

function exportarPDF() {
  const paraExportar = selecionados.value.size > 0
    ? registros.value.filter(r => selecionados.value.has(r.id))
    : registrosFiltrados.value

  const janela = window.open('', '', 'width=800,height=600')
  if (janela) {
    const tabelaHtml = `
      <h2>Registros de Ponto</h2>
      <table border="1" cellpadding="5" cellspacing="0" style="border-collapse: collapse; width: 100%;">
        <thead>
          <tr>
            <th>Empresa</th>
            <th>Funcionário</th>
            <th>Dia</th>
            <th>Entrada</th>
            <th>Saída</th>
            <th>Observações</th>
          </tr>
        </thead>
        <tbody>
          ${paraExportar.map(r => `
            <tr>
              <td>${r.empresa}</td>
              <td>${r.funcionario}</td>
              <td>${r.diaTrabalhado}</td>
              <td>${r.horarioEntrada}</td>
              <td>${r.horarioSaida}</td>
              <td>${r.observacoes}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    `
    janela.document.write(`
      <html>
        <head><title>Registros de Ponto</title></head>
        <body>${tabelaHtml}</body>
      </html>
    `)
    janela.document.close()
    janela.print()
  }
}
</script>

<template>
  <div class="grid justify-items-center">
    <UCard class="w-11/12 md:w-4/5">
      <template #header>
        <div class="flex justify-between items-center w-full flex-wrap gap-2">
          <h2 class="text-xl font-bold">Registros de Ponto</h2>
          <div class="flex gap-2 flex-wrap">
            <UButton color="primary" @click="abrirModalParaCadastro">Novo Registro</UButton>
            <UButton color="white" @click="exportarCSV" icon="heroicons:arrow-down-tray">
              Exportar CSV
            </UButton>
            <UButton color="white" @click="exportarPDF" icon="heroicons:arrow-down-tray">
              Exportar PDF
            </UButton>
            <UPopover>
              <UButton icon="heroicons:funnel-solid" />
              <template #panel>
                <div class="p-4">
                  <URadioGroup v-model="filtroSelecionado" legend="Filtrar por" :options="filtroOptions" />
                </div>
              </template>
            </UPopover>
          </div>
        </div>
      </template>

      <UInput v-model="termoPesquisa" placeholder="Buscar registro..." class="mt-2" />

      <table class="w-full border-collapse border border-gray-300 dark:border-gray-600 mt-4">
        <thead>
          <tr class="bg-gray-100 dark:bg-gray-800 text-center">
            <th>
              <input
                type="checkbox"
                :checked="todosSelecionadosNaPagina()"
                @change="toggleSelecionarTodos"
                aria-label="Selecionar todos"
              />
            </th>
            <th>Empresa</th>
            <th>Funcionário</th>
            <th>Dia</th>
            <th>Entrada</th>
            <th>Saída</th>
            <th>Observações</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="registro in registrosPaginados"
            :key="registro.id"
            class="text-center"
          >
            <td>
              <input
                type="checkbox"
                :checked="selecionados.has(registro.id)"
                @change="() => toggleSelecionado(registro.id)"
                aria-label="Selecionar registro"
              />
            </td>
            <td>{{ registro.empresa }}</td>
            <td>{{ registro.funcionario }}</td>
            <td>{{ registro.diaTrabalhado }}</td>
            <td>{{ registro.horarioEntrada }}</td>
            <td>{{ registro.horarioSaida }}</td>
            <td>{{ registro.observacoes }}</td>
            <td>
              <div class="flex justify-center gap-2">
                <UButton icon="heroicons:pencil-square" color="primary" variant="ghost" @click="abrirModalParaEditar(registro)" />
                <UButton icon="heroicons:trash" color="red" variant="ghost" @click="abrirConfirmarExclusao(registro)" />
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="flex justify-end mt-4">
        <UPagination v-model="paginaAtual" :page-count="pageSize" :total="registrosFiltrados.length" />
      </div>
    </UCard>

    <!-- Modal de Cadastro -->
    <UModal v-model="modalAberto">
      <UCard class="w-full max-w-2xl mx-auto">
        <template #header>
          <h2 class="text-lg font-bold">Registro de Ponto</h2>
        </template>
        <div class="space-y-4">
          <UFormGroup label="Empresa"><UInput v-model="registroEditando.empresa" /></UFormGroup>
          <UFormGroup label="Funcionário"><UInput v-model="registroEditando.funcionario" /></UFormGroup>
          <UFormGroup label="Dia Trabalhado" hint="Formato: YYYY-MM-DD">
            <UInput v-model="registroEditando.diaTrabalhado" type="date" />
          </UFormGroup>
          <UFormGroup label="Horário de Entrada"><UInput v-model="registroEditando.horarioEntrada" type="time" /></UFormGroup>
          <UFormGroup label="Horário de Saída"><UInput v-model="registroEditando.horarioSaida" type="time" /></UFormGroup>
          <UFormGroup label="Observações"><UInput v-model="registroEditando.observacoes" /></UFormGroup>
        </div>
        <template #footer>
          <div class="flex justify-end gap-2">
            <UButton color="gray" @click="fecharModal">Cancelar</UButton>
            <UButton color="primary" @click="salvarCadastro">Salvar</UButton>
          </div>
        </template>
      </UCard>
    </UModal>

    <!-- Modal de Exclusão -->
    <UModal v-model="confirmarExclusaoAberto">
      <UCard class="w-full max-w-2xl mx-auto">
        <template #header>
          <h2 class="text-lg font-bold">Confirmar Exclusão</h2>
        </template>
        <div class="py-4">
          <p>Tem certeza que deseja excluir o registro de <strong>{{ registroParaExcluir?.funcionario }}</strong> do dia <strong>{{ registroParaExcluir?.diaTrabalhado }}</strong>?</p>
        </div>
        <template #footer>
          <div class="flex justify-end gap-2">
            <UButton color="gray" @click="cancelarExclusao">Cancelar</UButton>
            <UButton color="red" @click="confirmarExclusao">Excluir</UButton>
          </div>
        </template>
      </UCard>
    </UModal>
  </div>
</template>
