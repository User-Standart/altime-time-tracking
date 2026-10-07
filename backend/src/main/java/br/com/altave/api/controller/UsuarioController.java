package br.com.altave.api.controller;

import br.com.altave.api.dto.LoginDTO;
import br.com.altave.api.model.Usuario;
import br.com.altave.api.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Verifica se o login existe
    @GetMapping("/login")
    public Boolean existeUsuario(@RequestParam String login) {
        return usuarioService.buscarPorLogin(login).isPresent();
    }

    // Cadastra novo usuário
    @PostMapping
    public Usuario criarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.salvar(usuario);
    }

    // Valida login e senha (opcional)
    @PostMapping("/login")
    public Boolean autenticarUsuario(@RequestBody LoginDTO loginDTO) {
        return usuarioService.verificarLoginESenha(loginDTO);
    }
}