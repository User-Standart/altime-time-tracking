package br.com.altave.api.controller;

import br.com.altave.api.model.Cargo;
import br.com.altave.api.model.Empresa;
import br.com.altave.api.model.Funcionario;
import br.com.altave.api.repository.CargoRepository;
import br.com.altave.api.repository.EmpresaRepository;
import br.com.altave.api.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private CargoRepository cargoRepository;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @GetMapping
    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAllComDetalhes();
    }

    @GetMapping("/empresa/{cnpj}")
    public List<Funcionario> listarPorEmpresa(@PathVariable String cnpj) {
        return funcionarioRepository.findByEmpresaCnpj(cnpj);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarPorId(@PathVariable Long id) {
        Optional<Funcionario> funcionario = funcionarioRepository.findByIdComDetalhes(id);
        return funcionario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Map<String, Object> payload) {
        try {
            String nome = (String) payload.get("nome");
            String cpf = (String) payload.get("cpf");
            String email = (String) payload.get("email");
            String cnpjEmpresa = (String) payload.get("empresa");
            Long cargoId = Long.valueOf(payload.get("cargo").toString());
            Integer cargaHoraria = Integer.valueOf(payload.get("cargaHoraria").toString());

            Optional<Empresa> empresaOpt = empresaRepository.findByCnpj(cnpjEmpresa);
            Optional<Cargo> cargoOpt = cargoRepository.findById(cargoId);

            if (empresaOpt.isEmpty() || cargoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empresa ou Cargo inválido");
            }

            Funcionario funcionario = new Funcionario();
            funcionario.setNome(nome);
            funcionario.setCpf(cpf);
            funcionario.setEmail(email);
            funcionario.setCargaHoraria(cargaHoraria);
            funcionario.setEmpresa(empresaOpt.get());
            funcionario.setCargo(cargoOpt.get());

            Funcionario salvo = funcionarioRepository.save(funcionario);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar funcionário");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(id);
        if (funcionarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            String nome = (String) payload.get("nome");
            String cpf = (String) payload.get("cpf");
            String email = (String) payload.get("email");
            String cnpjEmpresa = (String) payload.get("empresa");
            Long cargoId = Long.valueOf(payload.get("cargo").toString());
            Integer cargaHoraria = Integer.valueOf(payload.get("cargaHoraria").toString());

            Optional<Empresa> empresaOpt = empresaRepository.findByCnpj(cnpjEmpresa);
            Optional<Cargo> cargoOpt = cargoRepository.findById(cargoId);

            if (empresaOpt.isEmpty() || cargoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empresa ou Cargo inválido");
            }

            Funcionario funcionario = funcionarioOpt.get();
            funcionario.setNome(nome);
            funcionario.setCpf(cpf);
            funcionario.setEmail(email);
            funcionario.setCargaHoraria(cargaHoraria);
            funcionario.setEmpresa(empresaOpt.get());
            funcionario.setCargo(cargoOpt.get());

            Funcionario atualizado = funcionarioRepository.save(funcionario);
            return ResponseEntity.ok(atualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar funcionário");
        }
    }

    @PostMapping("/{id}/imagem")
    public ResponseEntity<String> uploadImagem(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file) {
        Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(id);
        if (funcionarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Funcionário não encontrado");
        }

        try {
            String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            File destino = new File(UPLOAD_DIR + filename);
            file.transferTo(destino);

            Funcionario funcionario = funcionarioOpt.get();
            funcionario.setImagem("/uploads/" + filename);
            funcionarioRepository.save(funcionario);

            return ResponseEntity.ok("Imagem enviada com sucesso");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar imagem");
        }
    }
}
