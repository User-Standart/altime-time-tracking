package br.com.altave.api.dto;

/**
 * Resposta do assistente e o modelo que a gerou.
 */
public record ChatResponseDTO(String answer, String model) {
}
