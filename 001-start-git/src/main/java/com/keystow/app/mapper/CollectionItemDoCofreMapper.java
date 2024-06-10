package com.keystow.app.mapper;

import com.keystow.app.controller.form.CollectionFormDto;
import com.keystow.app.model.collection.CollectionDeItensDoCofre;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Component
public class CollectionItemDoCofreMapper {

	private final ModelMapper modelMapper;

	private final CollectionUsuarioPermissionMapper collectionUsuarioPermissionMapper;

	private final CollectionGrupoPermissionMapper collectionGrupoPermissionMapper;

	private final ItemCollectionPesquisaDtoMapper itemCollectionPesquisaDtoMapper;

	public CollectionFormDto toFormDto(CollectionDeItensDoCofre collectionDeItensDoCofre) {
		CollectionFormDto collectionFormDto = modelMapper.map(collectionDeItensDoCofre, CollectionFormDto.class);
        collectionFormDto.addCupDtoList(collectionUsuarioPermissionMapper.toFormDtoList(collectionDeItensDoCofre.getCollectionsUsuariosPermissionsList()));
        collectionFormDto.addCgpDtoList(collectionGrupoPermissionMapper.toFormDtoList(collectionDeItensDoCofre.getCollectionsGruposPermissionsList()));
        collectionFormDto.addItensDtoList(itemCollectionPesquisaDtoMapper.toFormDtoList(collectionDeItensDoCofre.getItensList()));
		return collectionFormDto;
	}

	public CollectionDeItensDoCofre toModel(CollectionFormDto collectionFormDto) {
		CollectionDeItensDoCofre collection = modelMapper.map(collectionFormDto, CollectionDeItensDoCofre.class);
        collection.addCollectionsUsuariosPermissionsModelList(
                collectionUsuarioPermissionMapper.toModelList(collectionFormDto.getCollectionsUsuariosPermissionsDtoList()));
        collection.addCollectionsGrupoPermissionsModelList(
                collectionGrupoPermissionMapper.toModelList(collectionFormDto.getCollectionsGruposPermissionsDtoList()));
		collection.addItensModelList(itemCollectionPesquisaDtoMapper.toModelList(collectionFormDto.getItensDtoList()));
		return collection;
	}

}
