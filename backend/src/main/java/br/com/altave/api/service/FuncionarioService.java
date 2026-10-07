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

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.altave.api.model.Contrato;
import br.com.altave.api.model.Funcionario;
import br.com.altave.api.repository.ContratoRepository;
import br.com.altave.api.repository.FuncionarioRepository;

@Service
public class FuncionarioService {

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    private final FuncionarioRepository repository;
    private final ContratoRepository contratoRepository;

    public FuncionarioService(
        FuncionarioRepository repository,
        ContratoRepository contratoRepository
    ) {
        this.repository = repository;
        this.contratoRepository = contratoRepository;
    }

    public String salvarImagem(Long id, MultipartFile file) {
        return repository.findById(id).map(funcionario -> {
            try {
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path path = Paths.get(uploadDir, fileName);

                Files.copy(file.getInputStream(), path);

                String fileUrl = "/uploads/" + fileName;

                funcionario.setImagem(fileUrl);
                repository.save(funcionario);

                return fileUrl;
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar a imagem", e);
            }
        }).orElse(null);
    }

    public String recuperarImagem(Long id) {
        return repository.findById(id).map(Funcionario::getImagem).orElse(null);
    }

    public List<Funcionario> listarTodos() {
        return repository.findAllComDetalhes();
    }

    public Optional<Funcionario> buscarPorId(Long id) {
        return repository.findByIdComDetalhes(id);
    }

    public Funcionario salvar(Funcionario funcionario) {
        Funcionario funcionarioSalvo = repository.save(funcionario);

        Contrato contrato = new Contrato();
        contrato.setFuncionario(funcionarioSalvo);
        contrato.setEmpresa(funcionarioSalvo.getEmpresa());

        Contrato contratoSalvo = contratoRepository.save(contrato);

        funcionarioSalvo.setContrato(contratoSalvo);

        return repository.save(funcionarioSalvo);
    }

    public Optional<Funcionario> atualizar(Long id, Funcionario funcionarioAtualizado) {
        return repository.findById(id).map(funcionario -> {
            funcionario.setNome(funcionarioAtualizado.getNome());
            funcionario.setCpf(funcionarioAtualizado.getCpf());
            funcionario.setFuncao(funcionarioAtualizado.getFuncao());
            funcionario.setEmail(funcionarioAtualizado.getEmail());
            funcionario.setCargaHoraria(funcionarioAtualizado.getCargaHoraria());
            funcionario.setCargo(funcionarioAtualizado.getCargo());
            funcionario.setEmpresa(funcionarioAtualizado.getEmpresa());

            if (funcionarioAtualizado.getContrato() != null && funcionarioAtualizado.getContrato().getId() == null) {
                Contrato contrato = funcionarioAtualizado.getContrato();
                contrato.setFuncionario(funcionario);
                contrato.setEmpresa(funcionario.getEmpresa());
                funcionario.setContrato(contratoRepository.save(contrato));
            }

            return repository.save(funcionario);
        });
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

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
