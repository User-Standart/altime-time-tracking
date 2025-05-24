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

import br.com.altave.api.model.Contrato;
import br.com.altave.api.model.Funcionario;
import br.com.altave.api.repository.ContratoRepository;
import br.com.altave.api.repository.FuncionarioRepository;

@Service
public class FuncionarioService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final FuncionarioRepository repository;
    private final ContratoRepository contratoRepository;

    public FuncionarioService(
        FuncionarioRepository repository,
        ContratoRepository contratoRepository
    ) {
        this.repository = repository;
        this.contratoRepository = contratoRepository;
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

    // Listar todos os funcionários com dados completos
    public List<Funcionario> listarTodos() {
        return repository.findAllComDetalhes();
    }

    // Buscar funcionário por ID com dados completos
    public Optional<Funcionario> buscarPorId(Integer id) {
        return repository.findByIdComDetalhes(id);
    }

    // Salvar um novo funcionário e criar contrato automaticamente
    public Funcionario salvar(Funcionario funcionario) {
        // Salva primeiro o funcionário para obter o ID
        Funcionario funcionarioSalvo = repository.save(funcionario);

        // Cria o contrato associado ao funcionário salvo
        Contrato contrato = new Contrato();
        contrato.setFuncionario(funcionarioSalvo);
        contrato.setEmpresa(funcionarioSalvo.getEmpresa());

        // Salva o contrato
        Contrato contratoSalvo = contratoRepository.save(contrato);

        // Associa o contrato ao funcionário
        funcionarioSalvo.setContrato(contratoSalvo);

        // Atualiza o funcionário com o contrato vinculado
        return repository.save(funcionarioSalvo);
    }

    // Atualizar um funcionário existente
    public Optional<Funcionario> atualizar(Integer id, Funcionario funcionarioAtualizado) {
        return repository.findById(id).map(funcionario -> {
            // Atualiza campos básicos
            funcionario.setNome(funcionarioAtualizado.getNome());
            funcionario.setCpf(funcionarioAtualizado.getCpf());
            funcionario.setFuncao(funcionarioAtualizado.getFuncao());
            funcionario.setEmail(funcionarioAtualizado.getEmail());
            funcionario.setCargaHoraria(funcionarioAtualizado.getCargaHoraria());
            funcionario.setCargo(funcionarioAtualizado.getCargo());
            funcionario.setEmpresa(funcionarioAtualizado.getEmpresa());

            // Se tiver contrato novo, salva também
            if (funcionarioAtualizado.getContrato() != null && funcionarioAtualizado.getContrato().getId() == null) {
                Contrato contrato = funcionarioAtualizado.getContrato();
                contrato.setFuncionario(funcionario);
                contrato.setEmpresa(funcionario.getEmpresa());
                funcionario.setContrato(contratoRepository.save(contrato));
            }

            // Salva o funcionário atualizado
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