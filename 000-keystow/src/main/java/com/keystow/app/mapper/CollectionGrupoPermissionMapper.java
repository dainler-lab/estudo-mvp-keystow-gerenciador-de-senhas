package com.keystow.app.mapper;

import com.keystow.app.controller.list.CollectionGrupoPermissionDto;
import com.keystow.app.model.collection.CollectionGrupoPermission;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@AllArgsConstructor
@Component
public class CollectionGrupoPermissionMapper {

	private final ModelMapper modelMapper;

	public CollectionGrupoPermissionDto toFormDto(CollectionGrupoPermission collectionGrupoPermission) {
		CollectionGrupoPermissionDto collectionGrupoPermissionDto = modelMapper.map(collectionGrupoPermission,
				CollectionGrupoPermissionDto.class);
		collectionGrupoPermissionDto.setGrupoNome(collectionGrupoPermission.getGrupo().getNome());
		return collectionGrupoPermissionDto;
	}

	public CollectionGrupoPermission toModel(CollectionGrupoPermissionDto collectionGrupoPermissionDto) {
		return modelMapper.map(collectionGrupoPermissionDto, CollectionGrupoPermission.class);
	}

	public List<CollectionGrupoPermissionDto> toFormDtoList(
			List<CollectionGrupoPermission> collectionGrupoPermissionList) {
		return collectionGrupoPermissionList.stream().map(this::toFormDto).toList();
	}

	public List<CollectionGrupoPermission> toModelList(
			List<CollectionGrupoPermissionDto> collectionGrupoPermissionDtoList) {
		return collectionGrupoPermissionDtoList.stream().map(this::toModel).toList();
	}

}
