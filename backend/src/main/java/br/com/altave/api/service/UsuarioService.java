package br.com.altave.api.service;

import br.com.altave.api.dto.LoginDTO;
import br.com.altave.api.model.Usuario;
import br.com.altave.api.repository.UsuarioRepository;
import br.com.altave.api.utils.PasswordUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Optional<Usuario> buscarPorLogin(String login) {
        return usuarioRepository.findByLogin(login);
    }

    public Usuario salvar(Usuario usuario) {
        if (usuarioRepository.findByLogin(usuario.getLogin()).isPresent()) {
            throw new RuntimeException("Login já cadastrado!");
        }

        // Criptografa a senha antes de salvar
        usuario.setSenha(PasswordUtil.hashSenha(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    public boolean verificarLoginESenha(LoginDTO loginDTO) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(loginDTO.getLogin());
        if (usuarioOpt.isPresent()) {
            String senhaCriptografada = PasswordUtil.hashSenha(loginDTO.getSenha());
            return senhaCriptografada.equals(usuarioOpt.get().getSenha());
        }
        return false;
    }
}