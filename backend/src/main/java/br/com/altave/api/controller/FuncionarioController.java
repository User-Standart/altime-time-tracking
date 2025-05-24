package br.com.altave.api.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import br.com.altave.api.model.Funcionario;
import br.com.altave.api.service.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FuncionarioController {

    private final FuncionarioService service;
    private final Path uploadDir;

    public FuncionarioController(FuncionarioService service, @Value("${file.upload-dir}") String uploadDirPath) {
        this.service = service;
        this.uploadDir = Paths.get(uploadDirPath);
    }

    @Operation(description = "Busca todos os funcionários e retorna seus dados completos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de funcionários retornada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Nenhum funcionário encontrado.")
    })
    @GetMapping
    public ResponseEntity<List<Funcionario>> listarTodos() {
        List<Funcionario> funcionarios = service.listarTodos();
        return !funcionarios.isEmpty() ? ResponseEntity.ok(funcionarios) : ResponseEntity.notFound().build();
    }

    @Operation(description = "Busca um funcionário específico pelo ID e retorna seus dados completos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionário encontrado e retornado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarPorId(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(description = "Cria um novo funcionário com os dados fornecidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para criação do funcionário.")
    })
    @PostMapping
    public ResponseEntity<Funcionario> salvar(@RequestBody Funcionario funcionario) {
        Funcionario salvo = service.salvar(funcionario);
        return ResponseEntity.status(201).body(salvo);
    }

    @Operation(description = "Atualiza os dados de um funcionário existente pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para atualização.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Funcionario> atualizar(
            @PathVariable Integer id,
            @RequestBody Funcionario funcionarioAtualizado) {
        return service.atualizar(id, funcionarioAtualizado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(description = "Deleta um funcionário pelo ID informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Funcionário deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Faz o upload da imagem de um funcionário pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagem enviada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Funcionário não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao salvar a imagem.")
    })
    @PostMapping("/{id}/imagem")
    public ResponseEntity<String> uploadImagem(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = service.salvarImagem(id, file);
        if (imageUrl != null) {
            return ResponseEntity.ok("Imagem salva com sucesso: " + imageUrl);
        }
        return ResponseEntity.status(404).body("Funcionário não encontrado");
    }

    @Operation(description = "Recupera a URL da imagem de um funcionário pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL da imagem retornada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Nenhuma imagem encontrada para o funcionário informado."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar a solicitação.")
    })
    @GetMapping("/{id}/imagem")
    public ResponseEntity<String> getImagem(@PathVariable Integer id) {
        try {
            String imageUrl = service.recuperarImagem(id);
            return imageUrl != null ? ResponseEntity.ok(imageUrl) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = uploadDir.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/por-empresa")
    public ResponseEntity<Map<String, Long>> getFuncionariosPorEmpresa() {
        Map<String, Long> dados = service.getFuncionariosPorEmpresa();
        return ResponseEntity.ok(dados);
    }
}