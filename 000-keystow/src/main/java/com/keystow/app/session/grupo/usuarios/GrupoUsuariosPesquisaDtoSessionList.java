package com.keystow.app.session.grupo.usuarios;

import com.keystow.app.controller.list.GrupoUsuarioPesquisaDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
class GrupoUsuariosPesquisaDtoSessionList {

    private final String uuid;

    private final List<GrupoUsuarioPesquisaDto> gupDtoList = new ArrayList<>();

    public GrupoUsuariosPesquisaDtoSessionList(String uuid) {
        this.uuid = uuid;
    }

    private Optional<GrupoUsuarioPesquisaDto> getGupDtoById(String gupDtoId) {
        return gupDtoList.stream().filter(gup -> gup.getId().equals(gupDtoId)).findAny();
    }

    public void add(GrupoUsuarioPesquisaDto grupoUsuarioPesquisaDto) {
        if (getGupDtoById(grupoUsuarioPesquisaDto.getId()).isPresent() || grupoUsuarioPesquisaDto.getId() == null) return;
        gupDtoList.add(grupoUsuarioPesquisaDto);
    }

    public void delete(GrupoUsuarioPesquisaDto grupoUsuarioPesquisaDto) {
        gupDtoList.removeIf(gu -> gu.getId().equals(grupoUsuarioPesquisaDto.getId()));
    }

}
