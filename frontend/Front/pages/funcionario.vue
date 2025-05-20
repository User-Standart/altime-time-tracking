<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import * as XLSX from 'xlsx';
import { jsPDF } from 'jspdf';
import axios from 'axios';
import { useToast } from '#imports';
import { cadastrarFuncionario, uploadImagemFuncionario } from '@/services/api';

const funcionarios = ref<any[]>([]);
const funcionariosSelecionados = ref<any[]>([]);
const termoPesquisa = ref('');
const filtroSelecionado = ref('nome');
const currentPage = ref(1);
const pageSize = 10;

const options = [
  { value: 'nome', label: 'Nome' },
  { value: 'cpf', label: 'CPF' },
  { value: 'empresa', label: 'Empresa' },
  { value: 'funcao', label: 'Função' }
];

// Cadastro modal
const modalAberto = ref(false);
const funcionario = ref({
 foto: null as File | null,
  fotoUrl: '' ,
  id: null,
  nome: '',
  cpf: '',
  empresa: '',
  cargaHoraria: '',
  funcao: '',
  email: ''
});
const toast = useToast();

const abrirModal = () => {
  modalAberto.value = true;
};

const fecharModal = () => {
  modalAberto.value = false;
  funcionario.value = {
   foto: null,
    fotoUrl: '',
    id: null,
    nome: '',
    cpf: '',
    empresa: '',
    cargaHoraria: '',
    funcao: '',
    email: ''
  };
};

const editarFuncionario = (f: any) => {
  funcionario.value = {
    foto: null,
    fotoUrl: formatarURLImagem(f.imagem),
    id: f.id,
    nome: f.nome || '',
    cpf: f.cpf || '',
    empresa: f.empresa || '',
    cargaHoraria: f.cargaHoraria || '',
    funcao: f.funcao || '',
    email: f.email || '',
    
  };
  modalAberto.value = true;
};

const fetchFuncionarios = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/funcionarios');
    funcionarios.value = response.data;
  } catch (error) {
    console.error('Erro ao carregar os funcionários', error);
  }
};
onMounted(fetchFuncionarios);

const formatarURLImagem = (caminhoRelativo: string) => {
  if (!caminhoRelativo) return '';
  return `http://localhost:8080${caminhoRelativo}`;
};

const salvarCadastro = async () => {
  try {
    let response;

    if (funcionario.value.id) {
      response = await axios.put(`http://localhost:8080/api/funcionarios/${funcionario.value.id}`, {
        nome: funcionario.value.nome,
        cpf: funcionario.value.cpf,
        empresa: funcionario.value.empresa,
        cargaHoraria: funcionario.value.cargaHoraria,
        funcao: funcionario.value.funcao,
        email: funcionario.value.email,
      });
    } else {
      response = await cadastrarFuncionario({
        nome: funcionario.value.nome,
        cpf: funcionario.value.cpf,
        empresa: funcionario.value.empresa,
        cargaHoraria: funcionario.value.cargaHoraria,
        funcao: funcionario.value.funcao,
        email: funcionario.value.email,
      });
    }

    const funcionarioId = funcionario.value.id || response.data.id;

    if (funcionario.value.foto) {
      await uploadImagemFuncionario(funcionarioId, funcionario.value.foto);
    }

    toast.add({
      id: 'sucesso',
      title: funcionario.value.id ? 'Funcionário atualizado com sucesso!' : 'Funcionário cadastrado com sucesso!',
      icon: 'i-heroicons-check-circle',
      timeout: 6000,
      color: 'green'
    });

    fecharModal();
    fetchFuncionarios();
  } catch (error) {
    console.error('Erro ao salvar:', error);
    toast.add({
      id: 'erro',
      title: 'Erro ao salvar',
      icon: 'i-heroicons-x-circle',
      timeout: 6000,
      color: 'red'
    });
  }
};

const handleFileUpload = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files.length > 0) {
    funcionario.value.foto = input.files[0];
    funcionario.value.fotoUrl = URL.createObjectURL(input.files[0]);
  }
};

const funcionariosFiltrados = computed(() => {
  if (!termoPesquisa.value) return funcionarios.value;
  return funcionarios.value.filter(f =>
    f[filtroSelecionado.value]?.toLowerCase().includes(termoPesquisa.value.toLowerCase())
  );
});

const totalPaginas = computed(() => Math.ceil(funcionariosFiltrados.value.length / pageSize));
const funcionariosPaginados = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return funcionariosFiltrados.value.slice(start, start + pageSize);
});

const exportToExcel = () => {
  if (funcionariosSelecionados.value.length === 0) {
    alert('Selecione pelo menos um funcionário para exportar!');
    return;
  }
  const dadosExportados = funcionariosSelecionados.value.map(({ imagem, ...rest }) => rest);
  const ws = XLSX.utils.json_to_sheet(dadosExportados);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, 'Funcionarios');
  XLSX.writeFile(wb, 'relatorio_funcionarios.xlsx');
};

const exportToPDF = () => {
  if (funcionariosSelecionados.value.length === 0) {
    alert('Selecione pelo menos um funcionário para exportar!');
    return;
  }
  const doc = new jsPDF();
  doc.setFontSize(12);
  doc.text('Relatório de Funcionários', 14, 20);
  let y = 30;
  const headers = ['Nome', 'CPF', 'Empresa', 'Função', 'E-mail'];
  headers.forEach((header, i) => {
    doc.text(header, 14 + i * 30, y);
  });
  y += 10;
  for (const f of funcionariosSelecionados.value) {
    doc.text(f.nome || '', 14, y);
    doc.text(f.cpf || '', 44, y);
    doc.text(f.empresa || '', 74, y);
    doc.text(f.funcao || '', 104, y);
    doc.text(f.email || '', 134, y);
    y += 10;
  }
  doc.save('relatorio_funcionarios.pdf');
};

// Nova modal de confirmação para deletar
const modalConfirmacaoAberta = ref(false);
const funcionarioParaDeletar = ref<any>(null);

const abrirModalConfirmacao = (f: any) => {
  funcionarioParaDeletar.value = f;
  modalConfirmacaoAberta.value = true;
};

const cancelarDelecao = () => {
  funcionarioParaDeletar.value = null;
  modalConfirmacaoAberta.value = false;
};

const confirmarDelecao = async () => {
  if (!funcionarioParaDeletar.value) return;
  try {
    await axios.delete(`http://localhost:8080/api/funcionarios/${funcionarioParaDeletar.value.id}`);
    toast.add({
      title: 'Funcionário deletado com sucesso!',
      icon: 'i-heroicons-check-circle',
      color: 'green'
    });
    modalConfirmacaoAberta.value = false;
    funcionarioParaDeletar.value = null;
    fetchFuncionarios();
  } catch (error) {
    console.error('Erro ao deletar funcionário:', error);
    toast.add({
      title: 'Erro ao deletar funcionário',
      icon: 'i-heroicons-x-circle',
      color: 'red'
    });
  }
};

// Substituir o método deletarFuncionario para abrir a modal
const deletarFuncionario = (f: any) => {
  abrirModalConfirmacao(f);
};

const page = currentPage;
const items = computed(() => funcionariosFiltrados.value);
</script>

<template>
  <div class="grid justify-items-center">
    <UCard class="w-3/4">
      <template #header>
        <div class="flex justify-between items-center w-full">
          <h2 class="text-xl font-bold">Relatório de Funcionários</h2>
          <div class="flex gap-2">
            <UButton color="primary" @click="abrirModal">Cadastrar Funcionário</UButton>
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

      <UInput v-model="termoPesquisa" placeholder="Digite para filtrar" class="mt-2" />

      <table class="w-full border-collapse border border-gray-300 dark:border-gray-600 mt-4">
        <thead>
          <tr class="bg-gray-100 dark:bg-gray-800">
            <th></th>
            <th class="text-center align-middle">Foto</th>
            <th class="text-center align-middle">Nome</th>
            <th class="text-center align-middle">CPF</th>
            <th class="text-center align-middle">Empresa</th>
            <th class="text-center align-middle">Função</th>
            <th class="text-center align-middle">Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(funcionario, index) in funcionariosPaginados" 
          :key="index" 
          class="text-center"
          >
            <td class="text-center"><input type="checkbox" v-model="funcionariosSelecionados" :value="funcionario" /></td>
            <td class="text-center"><img :src="formatarURLImagem(funcionario.imagem)" alt="Foto" class="w-12 h-12 rounded-full object-cover" /></td>
            <td class="align-middle">{{ funcionario.nome }}</td>
            <td class="align-middle">{{ funcionario.cpf }}</td>
            <td class="align-middle">{{ funcionario.empresa }}</td>
            <td class="align-middle">{{ funcionario.funcao }}</td>
            <td class="text-center flex gap-2 justify-center">
              <UButton icon="heroicons:pencil-square" color="primary" variant="ghost" @click="editarFuncionario(funcionario)" />
              <UButton icon="heroicons:trash" color="red" variant="ghost" @click="deletarFuncionario(funcionario)" />
            </td>
          </tr>
        </tbody>
      </table>

      <div class="flex justify-end mt-4">
        <UPagination v-model="page" :page-count="pageSize" :total="items.length" />
      </div>

      <template #footer>
        <div class="flex justify-end space-x-4 mt-4">
          <UButton @click="exportToPDF" color="primary">Exportar para PDF</UButton>
          <UButton @click="exportToExcel" color="green">Exportar para Excel</UButton>
        </div>
      </template>
    </UCard>

    <UModal v-model="modalAberto">
      <UCard class="w-full max-w-2xl mx-auto">
        <template #header>
          <h2 class="text-lg font-bold">Cadastro de Funcionário</h2>
        </template>
        <div class="space-y-4">
           <UFormGroup label="Foto"></UFormGroup>
            <input type="file" @change="handleFileUpload" accept="image/png, image/jpeg" />
            <div v-if="funcionario.fotoUrl" class="flex justify-center mt-2">
              <img :src="funcionario.fotoUrl" class="w-24 h-24 rounded-full object-cover" />
            </div>
          
          <UFormGroup label="Nome"><UInput v-model="funcionario.nome" /></UFormGroup>
          <UFormGroup label="CPF"><UInput v-model="funcionario.cpf" /></UFormGroup>
          <UFormGroup label="Empresa"><UInput v-model="funcionario.empresa" /></UFormGroup>
          <UFormGroup label="Carga Horária"><UInput v-model="funcionario.cargaHoraria" /></UFormGroup>
          <UFormGroup label="Função"><UInput v-model="funcionario.funcao" /></UFormGroup>
          <UFormGroup label="Email"><UInput v-model="funcionario.email" /></UFormGroup>
         
        </div>
        <template #footer>
          <div class="flex justify-end gap-2">
            <UButton @click="fecharModal" color="gray">Cancelar</UButton>
            <UButton @click="salvarCadastro" color="primary">Salvar</UButton>
          </div>
        </template>
      </UCard>
    </UModal>

    <!-- Modal de confirmação -->
    <UModal v-model="modalConfirmacaoAberta">
     <UCard class="w-full max-w-2xl mx-auto">
        <template #header>
          <h2 class="text-lg font-bold">Confirmação</h2>
        </template>
        <div>
          <p>Tem certeza que deseja deletar o funcionário <strong>{{ funcionarioParaDeletar?.nome }}</strong>?</p>
        </div>
        <template #footer>
          <div class="flex justify-end gap-2 mt-4">
            <UButton color="gray" @click="cancelarDelecao">Cancelar</UButton>
            <UButton color="red" @click="confirmarDelecao">Deletar</UButton>
          </div>
        </template>
      </UCard>
    </UModal>
  </div>
</template>

<style scoped>
/* Você pode ajustar estilos das modais aqui */
</style>
