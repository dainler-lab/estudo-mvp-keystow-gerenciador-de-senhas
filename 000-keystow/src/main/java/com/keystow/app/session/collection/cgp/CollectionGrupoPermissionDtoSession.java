package com.keystow.app.session.collection.cgp;

import com.keystow.app.controller.list.CollectionGrupoPermissionDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
class CollectionGrupoPermissionDtoSession {

	private final String uuid;

	private final List<CollectionGrupoPermissionDto> listCgpDtoSession = new ArrayList<>();

	public CollectionGrupoPermissionDtoSession(String uuid) {
		this.uuid = uuid;
	}

	private Optional<CollectionGrupoPermissionDto> getCgpDtoByGrupoId(String grupoId) {
		return listCgpDtoSession.stream().filter(cgp -> cgp.getGrupoId().equals(grupoId)).findAny();
	}

	public void adicionar(CollectionGrupoPermissionDto collectionGrupoPermissionDto) {
		if (getCgpDtoByGrupoId(collectionGrupoPermissionDto.getGrupoId()).isPresent()
				|| collectionGrupoPermissionDto.getGrupoId() == null)
			return;
		listCgpDtoSession.add(collectionGrupoPermissionDto);
	}

	public void atualizar(CollectionGrupoPermissionDto collectionGrupoPermissionDto) {
		// @formatter:off
        listCgpDtoSession.stream()
                            .filter(cgp -> cgp.getGrupoId().equals(collectionGrupoPermissionDto.getGrupoId()))
                            .findAny()
                            .ifPresent(cgp -> cgp.setPermission(collectionGrupoPermissionDto.getPermission()));
    }

    public void deletar(CollectionGrupoPermissionDto collectionGrupoPermissionDto) {
        listCgpDtoSession.removeIf(cgp -> cgp.getGrupoId().equals(collectionGrupoPermissionDto.getGrupoId()));
    }

}
