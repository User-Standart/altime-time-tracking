import axios, { type AxiosResponse } from 'axios';

export interface Empresa {
  id: number;
  nome: string;
  cnpj: string;
}

export interface Cargo {
  id: number;
  nomeCargo: string;
}

export interface Funcionario {
  id?: number;
  nome: string;
  cpf: string;
  email: string;
  cargaHoraria: string;
  empresa: string; // CNPJ
  cargo: number;   // ID do cargo
}

export interface RegistroPonto {
  empresa: string;
  funcionario: { id: number };
  diaTrabalhado: string;
  horarioEntrada: string;
  horarioSaida: string;
  observacoes?: string;
}

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// EMPRESAS
export const listarEmpresas = async (): Promise<AxiosResponse<Empresa[]>> => {
  return await api.get('/empresa');
};

// CARGOS
export const listarCargos = async (): Promise<AxiosResponse<Cargo[]>> => {
  return await axios.get('http://localhost:8080/cargo');
};

// FUNCIONÁRIOS
export const listarFuncionarios = async (): Promise<AxiosResponse<any[]>> => {
  return await api.get('/funcionarios');
};

export const cadastrarFuncionario = async (dados: Funcionario): Promise<AxiosResponse<any>> => {
  return await api.post('/funcionarios', dados);
};

export const atualizarFuncionario = async (id: number, dados: Funcionario): Promise<AxiosResponse<any>> => {
  return await api.put(`/funcionarios/${id}`, dados);
};

export const uploadImagemFuncionario = async (id: number, file: File): Promise<AxiosResponse<any>> => {
  const formData = new FormData();
  formData.append('file', file);
  return await api.post(`/funcionarios/${id}/imagem`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const getFuncionariosPorEmpresa = async (): Promise<AxiosResponse<Record<string, number>>> => {
  return await api.get('/funcionarios/por-empresa');
};

// REGISTRO DE PONTO
export const cadastrarRegistroDePonto = async (dados: RegistroPonto): Promise<AxiosResponse<RegistroPonto>> => {
  return await api.post('/registro', dados);
};

export default api;
