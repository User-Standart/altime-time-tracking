package br.com.altave.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.altave.api.model.RegistroDePonto;
import br.com.altave.api.service.RegistroDePontoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/registro")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RegistroDePontoController {

    private final RegistroDePontoService service;

    public RegistroDePontoController(RegistroDePontoService service) {
        this.service = service;
    }

    // Listar todos os registros
    @Operation(description = "Retorna uma lista com todos os registros de ponto cadastrados no sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de registros retornada com sucesso."),
        @ApiResponse(responseCode = "404", description = "Nenhum registro encontrado.")
    })
    @GetMapping
    public List<RegistroDePonto> listarTodos() {
        return service.listarTodos();
    }

    // Buscar registro por ID
    @Operation(description = "Busca um registro de ponto pelo seu ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro encontrado com sucesso."),
        @ApiResponse(responseCode = "404", description = "Nenhum registro encontrado para o ID informado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RegistroDePonto> buscarPorId(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Criar novo registro
    @Operation(description = "Cria um novo registro de ponto com os dados fornecidos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Registro criado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos para criação do registro.")
    })
    @PostMapping
    public RegistroDePonto salvar(@RequestBody RegistroDePonto registro) {
        return service.salvar(registro);
    }

    // Atualizar registro existente
    @Operation(description = "Atualiza os dados de um registro de ponto existente com base no ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso."),
        @ApiResponse(responseCode = "404", description = "Nenhum registro encontrado para o ID informado."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos para atualização.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RegistroDePonto> atualizar(@PathVariable Integer id, @RequestBody RegistroDePonto registroAtualizado) {
        return service.atualizar(id, registroAtualizado)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Deletar registro
    @Operation(description = "Deleta um registro de ponto com base no ID informado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Registro deletado com sucesso."),
        @ApiResponse(responseCode = "404", description = "Nenhum registro encontrado para o ID informado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}