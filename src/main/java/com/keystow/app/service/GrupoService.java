package com.keystow.app.service;

import com.keystow.app.controller.form.GrupoFormDto;
import com.keystow.app.controller.list.GrupoUsuarioPesquisaDto;
import com.keystow.app.exception.EntityNotFoundBusinessException;
import com.keystow.app.exception.ImpossivelExcluirEntidadeForeignKeyConstraintException;
import com.keystow.app.exception.NomeJaExisteException;
import com.keystow.app.mapper.GrupoMapper;
import com.keystow.app.model.Grupo;
import com.keystow.app.model.Usuario;
import com.keystow.app.repository.GrupoRepository;
import com.keystow.app.repository.ItemRepository;
import com.keystow.app.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GrupoService {

    private final GrupoMapper grupoMapper;
    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final ItemRepository itemRepository;

    public Grupo toModel(GrupoFormDto grupoFormDto) {
        return grupoMapper.toModel(grupoFormDto);
    }

    public GrupoFormDto toDto(Grupo grupo) {
        return grupoMapper.toFormDto(grupo);
    }

    @Transactional(readOnly = true)
    public Grupo getGrupoById(String id) {
        return grupoRepository.findById(id).orElseThrow(() -> new EntityNotFoundBusinessException("Grupo não encontrado"));
    }

    @Transactional(readOnly = true)
    public Grupo getGrupoByIdAndDonoId(String id, String donoId) {
        return grupoRepository.findByIdAndDonoId(id, donoId).orElseThrow(() -> new EntityNotFoundBusinessException("Grupo não encontrado"));
    }

    @Transactional
    public void salvarGrupoPorDono(GrupoFormDto grupoFormDto, String donoId) {
        Grupo grupo = prepararGrupo(grupoFormDto, donoId);
        atualizarUsuariosGrupo(grupo, grupoFormDto.getUsuarios());
        grupoRepository.save(grupo);
    }

    @SuppressWarnings("SpringTransactionalMethodCallsInspection")
    private Grupo prepararGrupo(GrupoFormDto grupoFormDto, String donoId) {
        Grupo grupo = toModel(grupoFormDto);
        Usuario dono = usuarioService.getUsuarioById(donoId);
        grupo.setDono(dono);

        verificarNomeGrupoExistente(grupo, donoId);
        configurarDatasGrupo(grupo);

        if (!grupo.isNew()) {
            Grupo grupoExistente = getGrupoById(grupo.getId());
            grupo.setUsuarios(new ArrayList<>(grupoExistente.getUsuarios()));
        } else {
            grupo.setUsuarios(new ArrayList<>());
        }

        return grupoRepository.save(grupo);
    }

    private void verificarNomeGrupoExistente(Grupo grupo, String donoId) {
        // @formatter:off
        grupoRepository.findByNomeIgnoreCaseAndDonoId(grupo.getNome(), donoId)
                       .filter(grupoExistente -> !grupoExistente.getId().equals(grupo.getId()))
                       .ifPresent(g -> {
                           throw new NomeJaExisteException("O nome do grupo já existe, tente outro novamente.");
                       }
        );
        // @formatter:on
    }

    @SuppressWarnings("SpringTransactionalMethodCallsInspection")
    private void configurarDatasGrupo(Grupo grupo) {
        if (grupo.isNew()) {
            grupo.setCriadoEm(LocalDateTime.now());
        } else {
            Grupo grupoExistente = getGrupoById(grupo.getId());
            grupo.setCriadoEm(grupoExistente.getCriadoEm());
        }
        grupo.setAtualizadoEm(LocalDateTime.now());
    }

    private void atualizarUsuariosGrupo(Grupo grupo, List<GrupoUsuarioPesquisaDto> usuariosDtoList) {
        // @formatter:off
        Set<Usuario> novosUsuarios = usuariosDtoList == null ? new HashSet<>() :
                                     usuariosDtoList.stream()
                                                    .map(usuarioDto -> usuarioService.getUsuarioById(usuarioDto.getId()))
                                                    .collect(Collectors.toSet());
        // @formatter:on
        atualizarUsuariosExistentes(grupo, novosUsuarios);
        adicionarNovosUsuarios(grupo, novosUsuarios);
    }

    private void atualizarUsuariosExistentes(Grupo grupo, Set<Usuario> novosUsuarios) {
        grupo.getUsuarios().removeIf(usuario -> {
            if (!novosUsuarios.contains(usuario)) {
                usuario.getGrupos().remove(grupo);
                usuarioRepository.save(usuario);
                return true;
            }
            return false;
        });
    }

    private void adicionarNovosUsuarios(Grupo grupo, Set<Usuario> novosUsuarios) {
        // @formatter:off
        novosUsuarios.stream()
                     .filter(novoUsuario -> !grupo.getUsuarios().contains(novoUsuario))
                     .forEach(novoUsuario -> {
                         grupo.getUsuarios().add(novoUsuario);
                         novoUsuario.getGrupos().add(grupo);
                         usuarioRepository.save(novoUsuario);
                     }
        );
        // @formatter:on
    }

    @Transactional
    public void excluirGrupoPorDono(String id, String donoId) {
        Grupo grupo = getGrupoByIdAndDonoId(id, donoId);
        if (itemRepository.existsByDonoId(grupo.getId()) || !grupo.getUsuarios().isEmpty()) {
            throw new ImpossivelExcluirEntidadeForeignKeyConstraintException(
                    "Não é possível excluir o grupo, pois existem itens ou membros vinculados a ele");
        }
        grupoRepository.delete(grupo);
    }
}
