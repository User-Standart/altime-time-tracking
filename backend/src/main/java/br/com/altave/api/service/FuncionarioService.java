package br.com.altave.api.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.altave.api.model.Funcionario;
import br.com.altave.api.repository.FuncionarioRepository;

@Service
public class FuncionarioService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository) {
        this.repository = repository;
    }

    // Salvar imagem de um funcionário
    public String salvarImagem(Integer id, MultipartFile file) {
        return repository.findById(id).map(funcionario -> {
            try {
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path path = Paths.get(uploadDir, fileName);

                Files.copy(file.getInputStream(), path);

                String fileUrl = "/api/funcionarios/uploads/" + fileName;

                funcionario.setImagem(fileUrl);
                repository.save(funcionario);

                return fileUrl;
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar a imagem", e);
            }
        }).orElse(null);
    }

    // Recuperar URL da imagem de um funcionário
    public String recuperarImagem(Integer id) {
        return repository.findById(id).map(Funcionario::getImagem).orElse(null);
    }

    // Listar todos os funcionários
    public List<Funcionario> listarTodos() {
        return repository.findAll();
    }

    // Buscar funcionário por ID
    public Optional<Funcionario> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    // Salvar um novo funcionário
    public Funcionario salvar(Funcionario funcionario) {
        return repository.save(funcionario);
    }

    // Atualizar um funcionário existente
    public Optional<Funcionario> atualizar(Integer id, Funcionario funcionarioAtualizado) {
        return repository.findById(id).map(funcionario -> {
            funcionario.setNome(funcionarioAtualizado.getNome());
            funcionario.setCpf(funcionarioAtualizado.getCpf());
            funcionario.setContrato(funcionarioAtualizado.getContrato());
            funcionario.setFuncao(funcionarioAtualizado.getFuncao());
            funcionario.setEmail(funcionarioAtualizado.getEmail());
            funcionario.setCargaHoraria(funcionarioAtualizado.getCargaHoraria());
            funcionario.setCargo(funcionarioAtualizado.getCargo());
            return repository.save(funcionario);
        });
    }

    // Deletar um funcionário
    public void deletar(Integer id) {
        repository.deleteById(id);
    }

    // Contar funcionários por empresa
    public Map<String, Long> getFuncionariosPorEmpresa() {
        List<Object[]> resultados = repository.countFuncionariosPorEmpresa();
        Map<String, Long> mapa = new HashMap<>();

        for (Object[] resultado : resultados) {
            String empresa = (String) resultado[0];
            Long quantidade = (Long) resultado[1];
            mapa.put(empresa, quantidade);
        }

        return mapa;
    }

    
}
