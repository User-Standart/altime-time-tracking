package br.com.altave.api.dto;

import java.util.List;

/**
 * Pergunta enviada ao assistente, com o histórico recente da conversa.
 */
public record ChatRequestDTO(String message, List<ChatMessageDTO> history) {
}
