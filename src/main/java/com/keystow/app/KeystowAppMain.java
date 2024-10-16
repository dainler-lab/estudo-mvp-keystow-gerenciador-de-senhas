package com.keystow.app;

import com.keystow.app.model.Usuario;
import com.keystow.app.service.UsuarioService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@AllArgsConstructor
public class KeystowAppMain {

//    private UsuarioService usuarioService;

    public static void main(String[] args) {
        SpringApplication.run(KeystowAppMain.class, args);
    }

//    @PostConstruct
//    public void init() {
//        Usuario usuario = new Usuario();
//        usuario.setNome("Administrador");
//        usuario.setEmail("admin@keystow.com");
//        usuario.setSenha("&35D479!&e8SW*4&$$3A*P");
//        usuario.setAtivo(true);
//        usuario.setDono(usuario);
//        usuarioService.save(usuario);
//    }

}
