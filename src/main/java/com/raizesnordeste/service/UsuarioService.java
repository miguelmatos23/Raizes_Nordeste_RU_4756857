package com.raizesnordeste.service;

import com.raizesnordeste.dto.UsuarioRequest;
import com.raizesnordeste.model.Perfil;
import com.raizesnordeste.model.Usuario;
import com.raizesnordeste.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public Usuario registrar(UsuarioRequest request) {
        if (repository.existsByEmail(request.email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome);
        usuario.setCpf(request.cpf);
        usuario.setTelefone(request.telefone);
        usuario.setEmail(request.email);
        usuario.setSenha(encoder.encode(request.senha));
        usuario.setPerfil(Perfil.valueOf(request.perfil));

        return repository.save(usuario);
    }

    public Usuario buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        repository.deleteById(id);
    }
}
