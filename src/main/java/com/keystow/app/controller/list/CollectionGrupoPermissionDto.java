package com.keystow.app.controller.list;

import com.keystow.app.controller.form.CollectionFormDto;
import com.keystow.app.model.enuns.AppPermission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CollectionGrupoPermissionDto {

	private String sessionUUID;

	private String id;

	@NotNull
	private CollectionFormDto collectionId;

	@NotNull
	private String grupoId;

	@NotNull
	@NotBlank
	private AppPermission permission;

	private String grupoNome;

	public CollectionGrupoPermissionDto(String grupoId, String grupoNome) {
		this.grupoId = grupoId;
		this.grupoNome = grupoNome;
	}

	@Override
	public String toString() {
		return "CGP DTO: " + this.grupoId;
	}

}
