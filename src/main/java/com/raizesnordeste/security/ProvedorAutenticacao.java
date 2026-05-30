package com.raizesnordeste.security;

import com.raizesnordeste.model.Usuario;
import com.raizesnordeste.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ProvedorAutenticacao implements AuthenticationProvider {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public ProvedorAutenticacao(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String senha = authentication.getCredentials().toString();

        // mesma mensagem nos dois casos pra nÃ£o revelar se o email existe ou nÃ£o
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Credenciais invÃ¡lidas"));

        if (!encoder.matches(senha, usuario.getSenha())) {
            throw new BadCredentialsException("Credenciais invÃ¡lidas");
        }

        return new AutenticacaoCustomizada(usuario);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
