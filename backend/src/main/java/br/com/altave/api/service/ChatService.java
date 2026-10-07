package br.com.altave.api.service;

import br.com.altave.api.dto.ChatMessageDTO;
import br.com.altave.api.dto.ChatRequestDTO;
import br.com.altave.api.dto.ChatResponseDTO;
import br.com.altave.api.model.Empresa;
import br.com.altave.api.model.Funcionario;
import br.com.altave.api.model.RegistroDePonto;
import br.com.altave.api.repository.EmpresaRepository;
import br.com.altave.api.repository.FuncionarioRepository;
import br.com.altave.api.repository.RegistroDePontoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Assistente de IA que responde perguntas sobre os dados do sistema usando um LLM local
 * (Mistral via Ollama). Os dados não saem do servidor e não há custo de API externa.
 */
@Service
public class ChatService {

    private static final int DIAS_DE_CONTEXTO = 30;
    private static final int LIMITE_HISTORICO = 10;
    private static final int LIMITE_REGISTROS_DETALHADOS = 50;

    private static final String INSTRUCOES = """
            You are the Altime assistant, an AI built into a time-tracking system.
            Answer the manager's questions using ONLY the company data provided below.
            If the answer is not in the data, say that you don't have that information.
            Never invent companies, employees, numbers or records.
            Be concise, show the relevant numbers and answer in the same language as the question
            (Brazilian Portuguese by default).""";

    private final EmpresaRepository empresaRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final RegistroDePontoRepository registroRepository;
    private final RestClient ollama;
    private final String modelo;

    public ChatService(EmpresaRepository empresaRepository,
                       FuncionarioRepository funcionarioRepository,
                       RegistroDePontoRepository registroRepository,
                       @Value("${ollama.base-url}") String ollamaUrl,
                       @Value("${ollama.model}") String modelo,
                       @Value("${ollama.timeout-seconds:120}") int timeoutSegundos) {
        this.empresaRepository = empresaRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.registroRepository = registroRepository;
        this.modelo = modelo;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(timeoutSegundos));
        this.ollama = RestClient.builder()
                .baseUrl(ollamaUrl)
                .requestFactory(requestFactory)
                .build();
    }

    /**
     * Envia a pergunta ao modelo junto com um resumo atualizado dos dados e o histórico recente.
     */
    @Transactional(readOnly = true)
    public ChatResponseDTO responder(ChatRequestDTO request) {
        List<Map<String, String>> mensagens = new ArrayList<>();
        mensagens.add(Map.of("role", "system", "content", INSTRUCOES + "\n\n" + montarContexto()));

        List<ChatMessageDTO> historico = Optional.ofNullable(request.history()).orElse(List.of()).stream()
                .filter(m -> m != null && m.content() != null && !m.content().isBlank())
                .filter(m -> "user".equals(m.role()) || "assistant".equals(m.role()))
                .toList();
        historico.subList(Math.max(0, historico.size() - LIMITE_HISTORICO), historico.size())
                .forEach(m -> mensagens.add(Map.of("role", m.role(), "content", m.content())));

        mensagens.add(Map.of("role", "user", "content", request.message().trim()));

        Map<String, Object> corpo = Map.of(
                "model", modelo,
                "messages", mensagens,
                "stream", false,
                "options", Map.of("temperature", 0.2));

        OllamaChatResponse resposta = ollama.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(corpo)
                .retrieve()
                .body(OllamaChatResponse.class);

        if (resposta == null || resposta.message() == null || resposta.message().content() == null) {
            throw new IllegalStateException("Resposta vazia do Ollama");
        }
        return new ChatResponseDTO(resposta.message().content().trim(), modelo);
    }

    /**
     * Resumo textual dos dados usado como contexto do modelo. CPF e e-mail ficam de fora.
     */
    private String montarContexto() {
        LocalDate hoje = LocalDate.now();
        List<Empresa> empresas = empresaRepository.findAll();
        List<Funcionario> funcionarios = funcionarioRepository.findAllComDetalhes();
        List<RegistroDePonto> registros = registroRepository
                .findByDataRegistroGreaterThanEqualOrderByDataRegistroDesc(hoje.minusDays(DIAS_DE_CONTEXTO).atStartOfDay());

        Map<String, Long> funcionariosPorEmpresa = funcionarios.stream()
                .filter(f -> f.getEmpresa() != null)
                .collect(Collectors.groupingBy(f -> f.getEmpresa().getCnpj(), Collectors.counting()));

        StringBuilder sb = new StringBuilder();
        sb.append("Today's date: ").append(hoje).append("\n\n");

        sb.append("COMPANIES (name | CNPJ | address | employees):\n");
        for (Empresa e : empresas) {
            sb.append("- ").append(e.getNome()).append(" | ").append(e.getCnpj()).append(" | ")
                    .append(e.getEndereco()).append(" | ")
                    .append(funcionariosPorEmpresa.getOrDefault(e.getCnpj(), 0L)).append('\n');
        }

        sb.append("\nEMPLOYEES (name | position | role | company | contracted hours):\n");
        for (Funcionario f : funcionarios) {
            sb.append("- ").append(f.getNome()).append(" | ")
                    .append(f.getCargo() != null ? f.getCargo().getNomeCargo() : "-").append(" | ")
                    .append(Objects.toString(f.getFuncao(), "-")).append(" | ")
                    .append(f.getEmpresa() != null ? f.getEmpresa().getNome() : "-").append(" | ")
                    .append(f.getCargaHoraria() != null ? f.getCargaHoraria() + "h" : "-").append('\n');
        }

        Map<Funcionario, List<RegistroDePonto>> registrosPorFuncionario = registros.stream()
                .collect(Collectors.groupingBy(RegistroDePonto::getFuncionario, LinkedHashMap::new, Collectors.toList()));

        sb.append("\nTIME ENTRIES IN THE LAST ").append(DIAS_DE_CONTEXTO)
                .append(" DAYS (employee | company | days recorded | hours worked | last entry):\n");
        if (registrosPorFuncionario.isEmpty()) {
            sb.append("- no time entries in this period\n");
        }
        registrosPorFuncionario.forEach((funcionario, lista) -> {
            long minutos = lista.stream().mapToLong(this::minutosTrabalhados).sum();
            sb.append("- ").append(funcionario.getNome()).append(" | ")
                    .append(funcionario.getEmpresa() != null ? funcionario.getEmpresa().getNome() : "-").append(" | ")
                    .append(lista.size()).append(" | ")
                    .append(String.format("%dh%02d", minutos / 60, minutos % 60)).append(" | ")
                    .append(lista.get(0).getDataRegistro().toLocalDate()).append('\n');
        });

        sb.append("\nMOST RECENT TIME ENTRIES (date | employee | clock-in | clock-out | note):\n");
        registros.stream().limit(LIMITE_REGISTROS_DETALHADOS).forEach(r ->
                sb.append("- ").append(r.getDataRegistro().toLocalDate()).append(" | ")
                        .append(r.getFuncionario().getNome()).append(" | ")
                        .append(r.getHorarioEntrada()).append(" | ")
                        .append(r.getHorarioSaida()).append(" | ")
                        .append(Objects.toString(r.getObservacao(), "-")).append('\n'));

        return sb.toString();
    }

    private long minutosTrabalhados(RegistroDePonto registro) {
        long minutos = Duration.between(registro.getHorarioEntrada(), registro.getHorarioSaida()).toMinutes();
        // Turno que passa da meia-noite
        return minutos < 0 ? minutos + 24 * 60 : minutos;
    }

    private record OllamaChatResponse(OllamaMessage message) {
    }

    private record OllamaMessage(String role, String content) {
    }
}
