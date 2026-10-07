package br.com.altave.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.altave.api.model.Empresa;
import br.com.altave.api.service.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/empresa")
public class EmpresaController {

    private final EmpresaService service;

    public EmpresaController(EmpresaService service) {
        this.service = service;
    }

    // Listar todas as empresas
    @Operation(description = "Retorna uma lista com todas as empresas cadastradas no sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de empresas retornada com sucesso."),
        @ApiResponse(responseCode = "404", description = "Nenhuma empresa encontrada.")
    })
    @GetMapping
    public ResponseEntity<List<Empresa>> listarTodos() {
        List<Empresa> empresas = service.listarTodos();
        return ResponseEntity.ok(empresas);
    }

    // Buscar empresa por CNPJ
    @Operation(description = "Busca uma empresa pelo seu CNPJ e retorna os dados cadastrados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empresa encontrada com sucesso."),
        @ApiResponse(responseCode = "404", description = "Nenhuma empresa encontrada para o CNPJ informado.")
    })
    @GetMapping("/{cnpj}")
    public ResponseEntity<Empresa> buscarPorCnpj(@PathVariable String cnpj) {
        return service.buscarPorCnpj(cnpj)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Criar uma nova empresa
    @Operation(description = "Cria um novo registro de empresa com os dados fornecidos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Empresa criada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos para criação da empresa.")
    })
    @PostMapping
    public ResponseEntity<Empresa> salvar(@RequestBody Empresa empresa) {
        Empresa novaEmpresa = service.salvar(empresa);
        return ResponseEntity.status(201).body(novaEmpresa);
    }

    // Atualizar uma empresa existente
    @Operation(description = "Atualiza os dados de uma empresa existente com base no CNPJ.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empresa atualizada com sucesso."),
        @ApiResponse(responseCode = "404", description = "Nenhuma empresa encontrada para o CNPJ informado."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos para atualização.")
    })
    @PutMapping("/{cnpj}")
    public ResponseEntity<Empresa> atualizar(@PathVariable String cnpj, @RequestBody Empresa empresaAtualizada) {
        return service.atualizar(cnpj, empresaAtualizada)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Deletar uma empresa
    @Operation(description = "Deleta uma empresa do sistema com base no CNPJ informado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Empresa deletada com sucesso."),
        @ApiResponse(responseCode = "404", description = "Nenhuma empresa encontrada para o CNPJ informado.")
    })
    @DeleteMapping("/{cnpj}")
    public ResponseEntity<Void> deletar(@PathVariable String cnpj) {
        service.deletar(cnpj);
        return ResponseEntity.noContent().build();
    }
}