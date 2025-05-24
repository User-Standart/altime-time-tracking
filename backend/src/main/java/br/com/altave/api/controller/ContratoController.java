package br.com.altave.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.altave.api.model.Contrato;
import br.com.altave.api.service.ContratoService;

@RestController
@RequestMapping("/contratos")
public class ContratoController {

    private final ContratoService service;

    public ContratoController(ContratoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Contrato> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contrato> buscarPorId(@PathVariable Integer id) {
        Optional<Contrato> contrato = service.buscarPorId(id);
        return contrato.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Contrato salvar(@RequestBody Contrato contrato) {
        return service.salvar(contrato);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contrato> atualizar(@PathVariable Integer id, @RequestBody Contrato contratoAtualizado) {
        Optional<Contrato> contrato = service.atualizar(id, contratoAtualizado);
        return contrato.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}