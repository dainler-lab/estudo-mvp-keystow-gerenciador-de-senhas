package com.keystow.app.session.grupo.usuarios;

import com.keystow.app.controller.list.GrupoUsuarioPesquisaDto;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SessionScope
@Component
public class ListasDeGrupoUsuariosDtoSession {

    private final Set<GrupoUsuariosPesquisaDtoSessionList> gupDtoSessionListSet = new HashSet<>();

    // @formatter:off
	private GrupoUsuariosPesquisaDtoSessionList getGupDtoSessionListByUUID(String uuid) {
		return gupDtoSessionListSet.stream()
								   .filter(grupoUsuariosPesquisaDtoSessionList -> grupoUsuariosPesquisaDtoSessionList.getUuid().equals(uuid))
								   .findAny()
								   .orElse(new GrupoUsuariosPesquisaDtoSessionList(uuid));
	}

    public List<GrupoUsuarioPesquisaDto> getListGupDtoSessionByUUID(String uuid) {
        return getGupDtoSessionListByUUID(uuid).getGupDtoList();
    }

    public void adicionar(String uuid, GrupoUsuarioPesquisaDto grupoUsuarioPesquisaDto) {
        GrupoUsuariosPesquisaDtoSessionList gupDtoSessionList  = getGupDtoSessionListByUUID(uuid);
        gupDtoSessionList.add(grupoUsuarioPesquisaDto);
        gupDtoSessionListSet.add(gupDtoSessionList);
    }

    public void excluir(GrupoUsuarioPesquisaDto grupoUsuarioPesquisaDto) {
        GrupoUsuariosPesquisaDtoSessionList gupDtoSessionList = getGupDtoSessionListByUUID(grupoUsuarioPesquisaDto.getSessionUUID());
        gupDtoSessionList.delete(grupoUsuarioPesquisaDto);
    }

}
