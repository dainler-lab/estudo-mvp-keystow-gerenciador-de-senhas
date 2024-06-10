package com.keystow.app.service;

import com.keystow.app.controller.form.GrupoFormDto;
import com.keystow.app.exception.EntityNotFoundBusinessException;
import com.keystow.app.exception.ImpossivelExcluirEntidadeForeignKeyConstraintException;
import com.keystow.app.exception.NomeJaExisteException;
import com.keystow.app.mapper.GrupoMapper;
import com.keystow.app.model.Grupo;
import com.keystow.app.model.Usuario;
import com.keystow.app.repository.GrupoRepository;
import com.keystow.app.repository.ItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GrupoService {

    private final GrupoMapper grupoMapper;

    private final GrupoRepository grupoRepository;

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

        Grupo grupo = toModel(grupoFormDto);

        Optional<Grupo> grupoOptional = grupoRepository.findByNomeIgnoreCaseAndDonoId(grupo.getNome(), donoId);

        if (grupoOptional.isPresent() && !grupoOptional.get().equals(grupo)) {
            throw new NomeJaExisteException("O nome do grupo já existe, tente outro novamente.");
        }

        if (grupo.isNew()) {
            grupo.setCriadoEm(LocalDateTime.now());
        } else {
            Grupo grupoExistente = getGrupoById(grupo.getId());
            grupo.setCriadoEm(grupoExistente.getCriadoEm());
            List<Usuario> usuariosExistentes = grupoExistente.getUsuarios();
            // Remover o grupo da lista de grupos dos usuários que foram removidos
            usuariosExistentes.forEach(usuarioExistente -> {
                if (grupoFormDto.getUsuarios().stream().noneMatch(usuarioDto -> usuarioDto.getId().equals(usuarioExistente.getId()))) {
                    usuarioExistente.getGrupos().remove(grupo);
                }
            });
        }

        List<Usuario> usuarios = new ArrayList<>();

        if (grupoFormDto.getUsuarios() != null && !grupoFormDto.getUsuarios().isEmpty()) {
            // @formatter:off
            usuarios = grupoFormDto.getUsuarios()
                                   .stream()
                                   .map(usuarioDto -> usuarioService.getUsuarioById(usuarioDto.getId())).toList();
            usuarios.forEach(usuario -> {
                if (!usuario.getGrupos().contains(grupo)) {
                    usuario.getGrupos().add(grupo);
                }
            });
            // @formatter:on
        }

        grupo.setUsuarios(usuarios);
        grupo.setDono(usuarioService.getUsuarioById(donoId));
        grupo.setAtualizadoEm(LocalDateTime.now());
        grupoRepository.save(grupo);
    }

    @Transactional
    public void excluirGrupoPorDono(String id, String donoId) {
        Grupo grupo = getGrupoByIdAndDonoId(id, donoId);
        boolean hasItems = itemRepository.existsByDonoId(grupo.getId());
        if (hasItems || !grupo.getUsuarios().isEmpty()) {
            throw new ImpossivelExcluirEntidadeForeignKeyConstraintException(
                    "Não é possível excluir o grupo, pois existem itens ou membros vinculados a ele");
        }
        grupoRepository.delete(grupo);
    }

}
