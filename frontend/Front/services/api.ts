import axios, { type AxiosResponse } from 'axios'

// Tipos das entidades
export interface Empresa {
  id: number
  nome: string
  cnpj: string
}

export interface Funcionario {
  id: number
  nome: string
}

export interface RegistroPonto {
  empresa: string // CNPJ da empresa
  funcionario: { id: number }
  diaTrabalhado: string // Ex: "2025-04-25"
  horarioEntrada: string // Ex: "08:00"
  horarioSaida: string // Ex: "17:00"
  observacoes?: string
}

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

// Empresa
export const cadastrarEmpresa = async (dados: Empresa): Promise<AxiosResponse<Empresa>> => {
  return await api.post('/empresa', dados)
}

export const listarEmpresas = async (): Promise<AxiosResponse<Empresa[]>> => {
  return await api.get('/empresa')
}

// Funcionário
export const cadastrarFuncionario = async (dados: Funcionario): Promise<AxiosResponse<Funcionario>> => {
  return await api.post('/funcionarios', dados)
}

export const listarFuncionarios = async (): Promise<AxiosResponse<Funcionario[]>> => {
  return await api.get('/funcionarios')
}

export const uploadImagemFuncionario = async (id: number, file: File): Promise<AxiosResponse> => {
  const formData = new FormData()
  formData.append('file', file)

  return await api.post(`/funcionarios/${id}/imagem`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

// Funcionário por empresa (corrigido e atualizado)
export const getFuncionariosPorEmpresa = async (): Promise<AxiosResponse<Record<string, string[]>>> => {
  return await api.get('/funcionarios/por-empresa')
}

// Registro de Ponto
export const cadastrarRegistroDePonto = async (
  dados: RegistroPonto
): Promise<AxiosResponse<RegistroPonto>> => {
  return await api.post('/registro', dados)
}

export default api
