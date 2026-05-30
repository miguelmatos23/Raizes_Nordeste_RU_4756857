package com.raizesnordeste.config;

import com.raizesnordeste.model.Perfil;
import com.raizesnordeste.model.Usuario;
import com.raizesnordeste.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInicial implements CommandLineRunner {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public AdminInicial(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        // cria o admin apenas na primeira execuÃ§Ã£o
        if (repository.findByEmail("admin@raizesnordeste.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@raizesnordeste.com");
            admin.setSenha(encoder.encode("admin123"));
            admin.setPerfil(Perfil.ADMIN);

            repository.save(admin);
            System.out.println("UsuÃ¡rio admin criado com sucesso");
        }
    }
}
