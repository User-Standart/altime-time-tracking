package br.com.altave.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.altave.api.model.Contrato;
import br.com.altave.api.model.Funcionario;
import br.com.altave.api.repository.ContratoRepository;
import br.com.altave.api.repository.FuncionarioRepository;

@Service
public class ContratoService {

    private final ContratoRepository repository;
    private final FuncionarioRepository funcionarioRepository;

    public ContratoService(ContratoRepository repository, FuncionarioRepository funcionarioRepository) {
        this.repository = repository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Contrato> listarTodos() {
        return repository.findAll();
    }

    public Optional<Contrato> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public Contrato salvar(Contrato contrato) {
        // Salva o contrato primeiro
        Contrato contratoSalvo = repository.save(contrato);

        // Associa o contrato ao funcionário
        Funcionario funcionario = contrato.getFuncionario();
        if (funcionario != null) {
            funcionario.setContrato(contratoSalvo);
            funcionarioRepository.save(funcionario);
        }

        return contratoSalvo;
    }

    public Optional<Contrato> atualizar(Integer id, Contrato contratoAtualizado) {
        return repository.findById(id).map(contrato -> {
            // Atualiza os campos relevantes
            contrato.setEmpresa(contratoAtualizado.getEmpresa());
            contrato.setFuncionario(contratoAtualizado.getFuncionario());

            // Salva o contrato atualizado
            Contrato contratoSalvo = repository.save(contrato);

            // Atualiza o contrato no funcionário
            Funcionario funcionario = contrato.getFuncionario();
            if (funcionario != null) {
                funcionario.setContrato(contratoSalvo);
                funcionarioRepository.save(funcionario);
            }

            return contratoSalvo;
        });
    }

    public void deletar(Integer id) {
        repository.deleteById(id);
    }
}