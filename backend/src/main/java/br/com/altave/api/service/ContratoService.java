package br.com.altave.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.altave.api.model.Contrato;
import br.com.altave.api.repository.ContratoRepository;

@Service
public class ContratoService {
    private final ContratoRepository repository;

    public ContratoService(ContratoRepository repository) {
        this.repository = repository;
    }

    public List<Contrato> listarTodos() {
        return repository.findAll();
    }

    public Optional<Contrato> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public Contrato salvar(Contrato contrato) {
        return repository.save(contrato);
    }

    public Optional<Contrato> atualizar(Integer id, Contrato contratoAtualizado) {
        return repository.findById(id).map(contrato -> {
            contrato.setEmpresa(contratoAtualizado.getEmpresa());
            contrato.setDataInicio(contratoAtualizado.getDataInicio());
            contrato.setDataFim(contratoAtualizado.getDataFim());
            return repository.save(contrato);
        });
    }

    public void deletar(Integer id) {
        repository.deleteById(id);
    }
}