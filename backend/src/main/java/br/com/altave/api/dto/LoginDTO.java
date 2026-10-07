package br.com.altave.api.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String login;
    private String senha;
}