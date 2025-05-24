<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import * as XLSX from 'xlsx';
import { jsPDF } from 'jspdf';
import axios from 'axios';
import { useToast } from '#imports';
import { cadastrarFuncionario, uploadImagemFuncionario } from '@/services/api';

const funcionarios = ref<any[]>([]);
const empresas = ref<any[]>([]);
const cargos = ref<any[]>([]);  // <-- adicionado
const funcionariosSelecionados = ref<any[]>([]);
const termoPesquisa = ref('');
const filtroSelecionado = ref('nome');
const currentPage = ref(1);
const pageSize = 10;

const options = [
  { value: 'nome', label: 'Nome' },
  { value: 'cpf', label: 'CPF' },
  { value: 'empresa', label: 'Empresa' },
  { value: 'cargo', label: 'Cargo' }
];

const modalAberto = ref(false);
const funcionario = ref({
  id: null,
  nome: '',
  cpf: '',
  empresa: '',
  cargaHoraria: '',
  cargo: '',      // campo cargo mantido como string
  email: '',
  foto: null as File | null,
  fotoUrl: ''
});

const toast = useToast();

const abrirModal = () => { modalAberto.value = true; };

const fecharModal = () => {
  modalAberto.value = false;
  funcionario.value = {
    id: null,
    nome: '',
    cpf: '',
    empresa: '',
    cargaHoraria: '',
    cargo: '',
    email: '',
    foto: null,
    fotoUrl: ''
  };
};

const editarFuncionario = (f: any) => {
  funcionario.value = {
    id: f.id,
    nome: f.nome || '',
    cpf: f.cpf || '',
    empresa: f.empresa || '',
    cargaHoraria: f.cargaHoraria || '',
    cargo: f.cargo || '',
    email: f.email || '',
    foto: null,
    fotoUrl: formatarURLImagem(f.imagem)
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

const fetchEmpresas = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/empresas');
    empresas.value = response.data;
  } catch (error) {
    console.error('Erro ao carregar empresas', error);
  }
};

const fetchCargos = async () => {   // <-- função nova para buscar cargos
  try {
    const response = await axios.get('http://localhost:8080/api/cargos');
    cargos.value = response.data;
  } catch (error) {
    console.error('Erro ao carregar cargos', error);
  }
};

onMounted(() => {
  fetchFuncionarios();
  fetchEmpresas();
  fetchCargos();  // <-- chamada nova
});

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
        cargo: funcionario.value.cargo,
        email: funcionario.value.email,
      });
    } else {
      response = await cadastrarFuncionario({
        nome: funcionario.value.nome,
        cpf: funcionario.value.cpf,
        empresa: funcionario.value.empresa,
        cargaHoraria: funcionario.value.cargaHoraria,
        cargo: funcionario.value.cargo,
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

const exportToCSV = () => {
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
  const headers = ['Nome', 'CPF', 'Empresa', 'Cargo', 'E-mail'];
  headers.forEach((header, i) => {
    doc.text(header, 14 + i * 30, y);
  });
  y += 10;
  for (const f of funcionariosSelecionados.value) {
    doc.text(f.nome || '', 14, y);
    doc.text(f.cpf || '', 44, y);
    doc.text(f.empresa || '', 74, y);
    doc.text(f.cargo || '', 104, y);
    doc.text(f.email || '', 134, y);
    y += 10;
  }
  doc.save('relatorio_funcionarios.pdf');
};
</script>

<template>
  <div class="grid justify-items-center">
    <UCard class="w-11/12 md:w-4/5">
      <template #header>
        <div class="flex justify-between items-center w-full flex-wrap gap-2">
          <h2 class="text-xl font-bold">Funcionários</h2>
          <div class="flex gap-2 flex-wrap">
            <UButton color="primary" @click="abrirModal">Cadastrar Funcionário</UButton>
            <UButton color="white" icon="heroicons:arrow-down-tray" @click="exportToCSV">
              Exportar CSV
            </UButton>
            <UButton color="white" icon="heroicons:arrow-down-tray" @click="exportToPDF">
              Exportar PDF
            </UButton>
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
            <th class="text-center">Foto</th>
            <th class="text-center">Nome</th>
            <th class="text-center">CPF</th>
            <th class="text-center">Empresa</th>
            <th class="text-center">Cargo</th>
            <th class="text-center">Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(funcionario, index) in funcionariosPaginados" :key="index" class="hover:bg-gray-50 hover:dark:bg-gray-800">
            <td class="text-center">
              <input type="checkbox" v-model="funcionariosSelecionados" :value="funcionario" />
            </td>
            <td class="text-center">
              <img :src="formatarURLImagem(funcionario.imagem)" alt="Foto" class="w-12 h-12 rounded-full object-cover" />
            </td>
            <td>{{ funcionario.nome }}</td>
            <td>{{ funcionario.cpf }}</td>
            <td>{{ funcionario.empresa }}</td>
            <td>{{ funcionario.cargo }}</td>
            <td class="text-center">
              <UButton icon="heroicons:pencil-square" color="primary" variant="ghost" @click="editarFuncionario(funcionario)" />
            </td>
          </tr>
        </tbody>
      </table>

      <div class="flex justify-end mt-4">
        <UPagination v-model="currentPage" :page-count="pageSize" :total="funcionariosFiltrados.length" />
      </div>
    </UCard>

    <UModal v-model="modalAberto">
      <UCard class="w-full max-w-2xl mx-auto">
        <template #header>
          <h2 class="text-lg font-bold">Cadastro de Funcionário</h2>
        </template>

        <div class="grid gap-4">
          <UFormGroup label="Foto">
            <input type="file" @change="handleFileUpload" accept="image/*" />
            <img v-if="funcionario.fotoUrl" :src="funcionario.fotoUrl" alt="Foto" class="w-24 h-24 rounded-full object-cover mt-2" />
          </UFormGroup>

          <UFormGroup label="Nome">
            <UInput v-model="funcionario.nome" />
          </UFormGroup>

          <UFormGroup label="CPF">
            <UInput v-model="funcionario.cpf" />
          </UFormGroup>

          <UFormGroup label="Empresa">
            <USelect
              v-model="funcionario.empresa"
              :options="empresas.map(e => ({ label: e.nome, value: e.nome }))"
              placeholder="Selecione uma empresa"
            />
          </UFormGroup>

          <UFormGroup label="Carga Horária">
            <UInput v-model="funcionario.cargaHoraria" />
          </UFormGroup>

          <UFormGroup label="Cargo">
            <USelect
              v-model="funcionario.cargo"
              :options="cargos.map(c => ({ label: c.nome, value: c.nome }))"
              placeholder="Selecione um cargo"
            />
          </UFormGroup>

          <UFormGroup label="Email">
            <UInput v-model="funcionario.email" />
          </UFormGroup>

          <div class="flex justify-end gap-2">
            <UButton color="primary" @click="salvarCadastro">Salvar</UButton>
            <UButton color="white" @click="fecharModal">Cancelar</UButton>
          </div>
        </div>
      </UCard>
    </UModal>
  </div>
</template>