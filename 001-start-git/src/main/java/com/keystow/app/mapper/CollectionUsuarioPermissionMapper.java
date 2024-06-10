package com.keystow.app.mapper;

import com.keystow.app.controller.list.CollectionUsuarioPermissionDto;
import com.keystow.app.model.collection.CollectionUsuarioPermission;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@AllArgsConstructor
@Component
public class CollectionUsuarioPermissionMapper {

	private final ModelMapper modelMapper;

	public CollectionUsuarioPermissionDto toFormDto(CollectionUsuarioPermission collectionUsuarioPermission) {
		CollectionUsuarioPermissionDto collectionUsuarioPermissionDto = modelMapper.map(collectionUsuarioPermission,
				CollectionUsuarioPermissionDto.class);
		collectionUsuarioPermissionDto.setUsuarioNome(collectionUsuarioPermission.getUsuario().getNome());
		collectionUsuarioPermissionDto.setUsuarioEmail(collectionUsuarioPermission.getUsuario().getEmail());
		return collectionUsuarioPermissionDto;
	}

	public CollectionUsuarioPermission toModel(CollectionUsuarioPermissionDto collectionUsuarioPermissionDto) {
		return modelMapper.map(collectionUsuarioPermissionDto, CollectionUsuarioPermission.class);
	}

	public List<CollectionUsuarioPermissionDto> toFormDtoList(
			List<CollectionUsuarioPermission> collectionUsuarioPermissionList) {
		return collectionUsuarioPermissionList.stream().map(this::toFormDto).toList();
	}

	public List<CollectionUsuarioPermission> toModelList(
			List<CollectionUsuarioPermissionDto> collectionUsuarioPermissionDtoList) {
		return collectionUsuarioPermissionDtoList.stream().map(this::toModel).toList();
	}

}
