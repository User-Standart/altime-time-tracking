package br.com.altave.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.altave.api.model.Empresa;
import br.com.altave.api.repository.EmpresaRepository;

@Service
public class EmpresaService {

    private final EmpresaRepository repository;

    public EmpresaService(EmpresaRepository repository) {
        this.repository = repository;
    }

    public List<Empresa> listarTodos() {
        return repository.findAll();
    }

    public Optional<Empresa> buscarPorCnpj(String cnpj) {
        return repository.findById(cnpj);
    }

    public Empresa salvar(Empresa empresa) {
        return repository.save(empresa);
    }

    public Optional<Empresa> atualizar(String cnpj, Empresa empresaAtualizada) {
        return repository.findById(cnpj).map(empresa -> {
            empresa.setNome(empresaAtualizada.getNome());
            empresa.setEndereco(empresaAtualizada.getEndereco());
            return repository.save(empresa);
        });
    }

    public void deletar(String cnpj) {
        repository.deleteById(cnpj);
    }
}