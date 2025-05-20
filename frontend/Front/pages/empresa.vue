<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useToast } from '#imports'

const empresas = ref<any[]>([])
const empresasSelecionadas = ref<any[]>([])
const termoPesquisa = ref('')
const filtroSelecionado = ref('nome')
const currentPage = ref(1)
const pageSize = 5
const toast = useToast()

const modalAberto = ref(false)
const empresa = ref({
  id: null,
  nome: '',
  cnpj: '',
  endereco: ''
})

// Modal de confirmação de exclusão
const confirmarExclusaoAberto = ref(false)
const empresaParaExcluir = ref<any>(null)

const options = [
  { value: 'nome', label: 'Nome' },
  { value: 'cnpj', label: 'CNPJ' }
]

const abrirModal = () => {
  modalAberto.value = true
}

const fecharModal = () => {
  modalAberto.value = false
  empresa.value = {
    id: null,
    nome: '', cnpj: '', endereco: ''
  }
}

const editarEmpresa = (e: any) => {
  empresa.value = {
    id: e.id,
    nome: e.nome || '',
    cnpj: e.cnpj || '',
    endereco: e.endereco || ''
  }
  abrirModal()
}

// Abre o modal de confirmação
const abrirConfirmacaoExclusao = (e: any) => {
  empresaParaExcluir.value = e
  confirmarExclusaoAberto.value = true
}

// Cancela exclusão
const cancelarExclusao = () => {
  empresaParaExcluir.value = null
  confirmarExclusaoAberto.value = false
}

// Confirma e deleta
const confirmarExclusao = async () => {
  if (!empresaParaExcluir.value) return
  await deletarEmpresa(empresaParaExcluir.value)
  confirmarExclusaoAberto.value = false
  empresaParaExcluir.value = null
}

const deletarEmpresa = async (empresa: any) => {
  try {
    await axios.delete(`http://localhost:8080/api/empresa/${empresa.cnpj}`)
    toast.add({
      id: 'sucesso-delete',
      title: 'Empresa deletada com sucesso!',
      icon: 'i-heroicons-trash',
      timeout: 5000,
      color: 'green'
    })
    fetchEmpresas()
  } catch (error) {
    console.error('Erro ao deletar empresa:', error)
    toast.add({
      id: 'erro-delete',
      title: 'Erro ao deletar empresa',
      icon: 'i-heroicons-x-circle',
      timeout: 6000,
      color: 'red'
    })
  }
}

const fetchEmpresas = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/empresa')
    empresas.value = response.data
  } catch (error) {
    console.error('Erro ao carregar empresa:', error)
  }
}
onMounted(fetchEmpresas)

const salvarCadastro = async () => {
  try {
    const payload = {
      nome: empresa.value.nome,
      cnpj: empresa.value.cnpj,
      endereco: empresa.value.endereco
    }

    if (empresa.value.id) {
      await axios.delete(`http://localhost:8080/api/empresa/${empresa.cnpj}`)
    } else {
      await axios.post('http://localhost:8080/api/empresa', payload)
    }

    toast.add({
      id: 'sucesso',
      title: 'Empresa salva com sucesso!',
      icon: 'i-heroicons-check-circle',
      timeout: 5000,
      color: 'green'
    })

    fecharModal()
    fetchEmpresas()
  } catch (error) {
    console.error('Erro ao salvar empresa:', error)
    toast.add({
      id: 'erro',
      title: 'Erro ao salvar empresa',
      icon: 'i-heroicons-x-circle',
      timeout: 6000,
      color: 'red'
    })
  }
}

const empresasFiltradas = computed(() => {
  if (!termoPesquisa.value) return empresas.value
  return empresas.value.filter(e =>
    e[filtroSelecionado.value]?.toLowerCase().includes(termoPesquisa.value.toLowerCase())
  )
})

const totalPaginas = computed(() => Math.ceil(empresasFiltradas.value.length / pageSize))
const empresasPaginadas = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return empresasFiltradas.value.slice(start, start + pageSize)
})

const page = currentPage
const items = computed(() => empresasFiltradas.value)
</script>

<template>
  <div class="grid justify-items-center">
    <UCard class="w-3/4">
      <template #header>
        <div class="flex justify-between items-center w-full">
          <h2 class="text-xl font-bold">Empresas</h2>
          <div class="flex gap-2">
            <UButton color="primary" @click="abrirModal">Cadastrar Empresa</UButton>
            <UPopover>
              <UButton icon="heroicons:funnel-solid" />
              <template #panel>
                <div class="p-4">
                  <URadioGroup v-model="filtroSelecionado" legend="Filtrar por" :options="options" />
                </div>
              </template>
            </UPopover>
          </div>
        </div>
      </template>

      <UInput v-model="termoPesquisa" placeholder="Buscar empresa..." class="mt-2" />

      <table class="w-full border-collapse border border-gray-300 dark:border-gray-600 mt-4">
      <thead>
  <tr class="bg-gray-100 dark:bg-gray-800">
    <th class="text-center align-middle">Nome</th>
    <th class="text-center align-middle">CNPJ</th>
    <th class="text-center align-middle">Endereço</th>
    <th class="text-center align-middle">Ações</th>
  </tr>
</thead>
<tbody>
  <tr
    v-for="(empresa, index) in empresasPaginadas"
    :key="index"
    class="text-center"
  >
    <td class="align-middle">{{ empresa.nome }}</td>
    <td class="align-middle">{{ empresa.cnpj }}</td>
    <td class="align-middle">{{ empresa.endereco }}</td>
    <td class="align-middle">
      <div class="flex justify-center items-center gap-2">
        <UButton icon="heroicons:pencil-square" color="primary" variant="ghost" @click="editarEmpresa(empresa)" />
        <UButton icon="heroicons:trash" color="red" variant="ghost" @click="abrirConfirmacaoExclusao(empresa)" />
      </div>
    </td>
  </tr>
</tbody>
      </table>

      <div class="flex justify-end mt-4">
        <UPagination v-model="page" :page-count="pageSize" :total="items.length" />
      </div>
    </UCard>

    <!-- Modal de Cadastro -->
    <UModal v-model="modalAberto">
     <UCard class="w-full max-w-2xl mx-auto">
        <template #header>
          <h2 class="text-lg font-bold">Cadastro de Empresa</h2>
        </template>
        <div class="space-y-4">
          <UFormGroup label="Nome"><UInput v-model="empresa.nome" /></UFormGroup>
          <UFormGroup label="CNPJ"><UInput v-model="empresa.cnpj" /></UFormGroup>
          <UFormGroup label="Endereço"><UInput v-model="empresa.endereco" /></UFormGroup>
        </div>
        <template #footer>
          <div class="flex justify-end gap-2">
            <UButton @click="fecharModal" color="gray">Cancelar</UButton>
            <UButton @click="salvarCadastro" color="primary">Salvar</UButton>
          </div>
        </template>
      </UCard>
    </UModal>

    <!-- Modal de Confirmação de Exclusão -->
    <UModal v-model="confirmarExclusaoAberto">
     <UCard class="w-full max-w-2xl mx-auto">
        <template #header>
          <h2 class="text-lg font-bold text-red-600">Confirmar Exclusão</h2>
        </template>
        <div class="space-y-2">
          <p>Você tem certeza que deseja excluir a empresa <strong>{{ empresaParaExcluir?.nome }}</strong>?</p>
          <p class="text-sm text-gray-500">Esta ação não poderá ser desfeita.</p>
        </div>
        <template #footer>
          <div class="flex justify-end gap-2">
            <UButton @click="cancelarExclusao" color="gray">Cancelar</UButton>
            <UButton @click="confirmarExclusao" color="red">Excluir</UButton>
          </div>
        </template>
      </UCard>
    </UModal>
  </div>
</template>
