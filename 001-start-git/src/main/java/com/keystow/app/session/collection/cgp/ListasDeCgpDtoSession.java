package com.keystow.app.session.collection.cgp;

import com.keystow.app.controller.list.CollectionGrupoPermissionDto;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SessionScope
@Component
public class ListasDeCgpDtoSession {

	private final Set<CollectionGrupoPermissionDtoSession> cgpDtoSession = new HashSet<>();

	// @formatter:off
	private CollectionGrupoPermissionDtoSession getListasDeCgpDtoSessionPorUUID(String uuid) {
		return cgpDtoSession.stream()
								   .filter(cgpList -> cgpList.getUuid().equals(uuid))
								   .findAny()
								   .orElse(new CollectionGrupoPermissionDtoSession(uuid));
	}

    public List<CollectionGrupoPermissionDto> getListCgpDtoSessionPorUUID(String uuid) {
        return getListasDeCgpDtoSessionPorUUID(uuid).getListCgpDtoSession();
    }

    public void addCgp(String uuid, CollectionGrupoPermissionDto collectionGrupoPermissionDto) {
        CollectionGrupoPermissionDtoSession cgpDtoSessionList = getListasDeCgpDtoSessionPorUUID(uuid);
        cgpDtoSessionList.adicionar(collectionGrupoPermissionDto);
        cgpDtoSession.add(cgpDtoSessionList);
    }

    public void alterarCgp(CollectionGrupoPermissionDto collectionGrupoPermissionDto) {
        CollectionGrupoPermissionDtoSession cgpDtoSessionList = getListasDeCgpDtoSessionPorUUID(collectionGrupoPermissionDto.getSessionUUID());
        cgpDtoSessionList.atualizar(collectionGrupoPermissionDto);
    }

    public void excluirCgp(CollectionGrupoPermissionDto collectionGrupoPermissionDto) {
        CollectionGrupoPermissionDtoSession cgpDtoSessionList = getListasDeCgpDtoSessionPorUUID(collectionGrupoPermissionDto.getSessionUUID());
        cgpDtoSessionList.deletar(collectionGrupoPermissionDto);
    }

}
