package com.keystow.app.service;

import com.keystow.app.controller.form.UsuarioFormDto;
import com.keystow.app.exception.EntityNotFoundBusinessException;
import com.keystow.app.mapper.UsuarioMapper;
import com.keystow.app.model.Usuario;
import com.keystow.app.repository.UsuarioRepository;
import com.keystow.app.service.enuns.StatusUsuario;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

@Service
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final UsuarioMapper usuarioMapper;

    private final PasswordEncoder passwordEncoder;

    public UsuarioFormDto toDto(Usuario usuario) {
        return usuarioMapper.toFormDto(usuario);
    }

    public Usuario toModel(UsuarioFormDto usuarioFormDto) {
        return usuarioMapper.toModel(usuarioFormDto);
    }

    @Transactional(readOnly = true)
    public Usuario getUsuarioById(String id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundBusinessException("Usuário não encontrado"));
    }

    @Transactional
    public Usuario save(Usuario usuario) {
        if (usuario.isNew()) {
            // CRIAR GRUPO DO USUARIO E ADD OS USERS QUE ELE CRIAR DENTRO
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            usuario.setCriadoEm(LocalDateTime.now());
        } else {
            Usuario usuarioSaved = getUsuarioById(usuario.getId());
            if (usuarioSaved.getSenha() != null && !usuarioSaved.getSenha().equals(usuario.getSenha())) {
                usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            }
            usuario.setCriadoEm(usuarioSaved.getCriadoEm());
        }
        usuario.setAtualizadoEm(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void alterarStatusSelecionados(Collection<String> ids, StatusUsuario statusUsuario) {
        // Verifica se todos os IDs existem
        long count = ids.stream().filter(id -> usuarioRepository.findById(id).isPresent()).count();
        // count = Arrays.stream(ids).filter(id ->
        // usuarioRepository.findById(id).isPresent()).count();
        if (count != ids.size()) {
            throw new EntityNotFoundBusinessException("Um ou mais usuários não foram encontrados.");
        }
        // Se todos os IDs existirem, executa a alteração de status
        statusUsuario.executar(ids, usuarioRepository);
    }

    @Transactional
    public void excluirSelecionados(String[] ids) {
        Arrays.stream(ids).forEach(id -> {
            usuarioRepository.findById(id).ifPresentOrElse(usuario -> {
                usuarioRepository.deleteById(id);
            }, () -> {
                throw new EntityNotFoundBusinessException("Usuário não encontrado");
            });
        });
    }

}
