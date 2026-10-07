package br.com.altave.api.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import br.com.altave.api.dto.ChatRequestDTO;
import br.com.altave.api.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final int TAMANHO_MAXIMO_MENSAGEM = 2000;

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // Conversar com o assistente de IA
    @Operation(description = "Responde perguntas em linguagem natural sobre empresas, funcionários e registros de ponto usando um LLM local (Mistral via Ollama).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resposta gerada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Mensagem vazia ou muito longa."),
        @ApiResponse(responseCode = "503", description = "Ollama indisponível ou modelo não carregado.")
    })
    @PostMapping
    public ResponseEntity<?> conversar(@RequestBody ChatRequestDTO request) {
        if (request == null || request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "A mensagem não pode ser vazia."));
        }
        if (request.message().length() > TAMANHO_MAXIMO_MENSAGEM) {
            return ResponseEntity.badRequest().body(Map.of("erro", "A mensagem deve ter no máximo " + TAMANHO_MAXIMO_MENSAGEM + " caracteres."));
        }
        try {
            return ResponseEntity.ok(chatService.responder(request));
        } catch (RestClientException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("erro", "Assistente de IA indisponível. Verifique se o Ollama está em execução com o modelo configurado."));
        }
    }
}
