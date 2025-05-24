package br.com.altave.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.altave.api.model.Cargo;
import br.com.altave.api.service.CargoService;

@RestController
@RequestMapping("/cargo")
public class CargoController {

    private final CargoService cargoService;

    public CargoController(CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @GetMapping
    public ResponseEntity<List<Cargo>> listarTodos() {
        List<Cargo> cargos = cargoService.listarTodos();
        return ResponseEntity.ok(cargos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cargo> buscarPorId(@PathVariable Long id) {
        Optional<Cargo> cargo = cargoService.buscarPorId(id);
        return cargo.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cargo> salvar(@RequestBody Cargo cargo) {
        Cargo novoCargo = cargoService.salvar(cargo);
        return new ResponseEntity<>(novoCargo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cargo> atualizar(@PathVariable Long id, @RequestBody Cargo cargoAtualizado) {
        Optional<Cargo> cargoAtual = cargoService.atualizar(id, cargoAtualizado);
        return cargoAtual.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cargoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}