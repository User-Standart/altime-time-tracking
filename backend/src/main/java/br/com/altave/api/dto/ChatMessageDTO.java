package br.com.altave.api.dto;

/**
 * Mensagem de uma conversa com o assistente (role: "user" ou "assistant").
 */
public record ChatMessageDTO(String role, String content) {
}
