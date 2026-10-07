package br.com.altave.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.altave.api.model.RegistroDePonto;
import br.com.altave.api.repository.RegistroDePontoRepository;

@Service
public class RegistroDePontoService {
    private final RegistroDePontoRepository repository;

    public RegistroDePontoService(RegistroDePontoRepository repository) {
        this.repository = repository;
    }

    public List<RegistroDePonto> listarTodos() {
        return repository.findAll();
    }

    public Optional<RegistroDePonto> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public RegistroDePonto salvar(RegistroDePonto registro) {
        return repository.save(registro);
    }

    public Optional<RegistroDePonto> atualizar(Integer id, RegistroDePonto registroAtualizado) {
        return repository.findById(id).map(registro -> {
            registro.setEmpresa(registroAtualizado.getEmpresa());
            registro.setFuncionario(registroAtualizado.getFuncionario());
            registro.setDataRegistro(registroAtualizado.getDataRegistro());
            registro.setHorarioEntrada(registroAtualizado.getHorarioEntrada());
            registro.setHorarioSaida(registroAtualizado.getHorarioSaida());
            registro.setObservacao(registroAtualizado.getObservacao());
            return repository.save(registro);
        });
    }

    public void deletar(Integer id) {
        repository.deleteById(id);
    }
}