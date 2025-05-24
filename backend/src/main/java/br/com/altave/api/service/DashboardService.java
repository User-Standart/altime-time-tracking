package br.com.altave.api.service;

import br.com.altave.api.model.Funcionario;
import br.com.altave.api.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final FuncionarioRepository funcionarioRepository;

    public DashboardService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    /**
     * Retorna mapa com empresas e número de funcionários
     */
    public Map<String, Integer> getFuncionariosPorEmpresa() {
        List<Object[]> resultados = funcionarioRepository.countFuncionariosPorEmpresa();
        Map<String, Integer> dados = new HashMap<>();

        for (Object[] resultado : resultados) {
            String cnpjEmpresa = (String) resultado[0];
            Long quantidade = (Long) resultado[1];
            dados.put(cnpjEmpresa, quantidade.intValue());
        }

        return dados;
    }

    /**
     * Retorna lista de funcionários por CNPJ da empresa
     */
    public List<Funcionario> getFuncionariosByCnpjEmpresa(String cnpj) {
        return funcionarioRepository.findByEmpresaCnpj(cnpj);
    }

    /**
     * Retorna nomes dos funcionários por CNPJ da empresa
     */
    public List<String> getNomesFuncionariosByCnpjEmpresa(String cnpj) {
        return getFuncionariosByCnpjEmpresa(cnpj).stream()
                .map(Funcionario::getNome)
                .collect(Collectors.toList());
    }

    /**
     * Retorna horas trabalhadas reais (usando o campo 'cargaHoraria' do funcionário)
     */
    public Map<String, Integer> getHorasTrabalhadas(String cnpj, String nomeFuncionario) {
        List<Funcionario> funcionarios = getFuncionariosByCnpjEmpresa(cnpj);

        if (nomeFuncionario != null && !nomeFuncionario.isEmpty()) {
            return funcionarios.stream()
                    .filter(f -> f.getNome().equalsIgnoreCase(nomeFuncionario))
                    .filter(f -> f.getCargaHoraria() != null)
                    .collect(Collectors.toMap(
                            Funcionario::getNome,
                            Funcionario::getCargaHoraria
                    ));
        } else {
            return funcionarios.stream()
                    .filter(f -> f.getCargaHoraria() != null)
                    .collect(Collectors.toMap(
                            Funcionario::getNome,
                            Funcionario::getCargaHoraria
                    ));
        }
    }
}