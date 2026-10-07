package br.com.altave.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.altave.api.model.Cargo;
import br.com.altave.api.repository.CargoRepository;

@Service
public class CargoService {

    private final CargoRepository repository;

    public CargoService(CargoRepository repository) {
        this.repository = repository;
    }

    public List<Cargo> listarTodos() {
        return repository.findAll();
    }

    public Optional<Cargo> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Cargo salvar(Cargo cargo) {
        return repository.save(cargo);
    }

    public Optional<Cargo> atualizar(Long id, Cargo cargoAtualizado) {
        return repository.findById(id).map(cargo -> {
            cargo.setNomeCargo(cargoAtualizado.getNomeCargo());
            cargo.setDescricao(cargoAtualizado.getDescricao());
            return repository.save(cargo);
        });
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}