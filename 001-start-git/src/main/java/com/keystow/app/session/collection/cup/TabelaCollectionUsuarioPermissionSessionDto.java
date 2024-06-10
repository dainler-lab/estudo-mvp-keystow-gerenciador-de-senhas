package com.keystow.app.session.collection.cup;

import com.keystow.app.controller.list.CollectionUsuarioPermissionDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
class TabelaCollectionUsuarioPermissionSessionDto {

	private final String uuid;

	private final List<CollectionUsuarioPermissionDto> cupSessionDtoList = new ArrayList<>();

	public TabelaCollectionUsuarioPermissionSessionDto(String uuid) {
		this.uuid = uuid;
	}

	private Optional<CollectionUsuarioPermissionDto> getCupSessionDtoByUsuarioId(String id) {
		return cupSessionDtoList.stream().filter(cup -> cup.getUsuarioId().equals(id)).findAny();
	}

	public void createCupSessionDto(CollectionUsuarioPermissionDto collectionUsuarioPermissionDto) {
		Optional<CollectionUsuarioPermissionDto> cupOptional = getCupSessionDtoByUsuarioId(
				collectionUsuarioPermissionDto.getUsuarioId());
		if (cupOptional.isPresent() || collectionUsuarioPermissionDto.getUsuarioId() == null)
			return;
		cupSessionDtoList.add(collectionUsuarioPermissionDto);
	}

	public void updateCupSessionDto(CollectionUsuarioPermissionDto collectionUsuarioPermissionDto) {
		Optional<CollectionUsuarioPermissionDto> cupOptional = getCupSessionDtoByUsuarioId(
				collectionUsuarioPermissionDto.getUsuarioId());
		cupOptional.ifPresent(cup -> cup.setPermission(collectionUsuarioPermissionDto.getPermission()));
	}

	public void deleteCupSessionDto(CollectionUsuarioPermissionDto collectionUsuarioPermissionDto) {
		Optional<CollectionUsuarioPermissionDto> cupOptional = getCupSessionDtoByUsuarioId(
				collectionUsuarioPermissionDto.getUsuarioId());
		cupOptional.ifPresent(cupSessionDtoList::remove);
	}

}
