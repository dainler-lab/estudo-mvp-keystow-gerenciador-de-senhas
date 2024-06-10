package com.keystow.app.controller.list;

import com.keystow.app.controller.form.CollectionFormDto;
import com.keystow.app.model.enuns.AppPermission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CollectionUsuarioPermissionDto {

	private String sessionUUID;

	private String id;

	@NotNull
	private CollectionFormDto collectionId;

	@NotNull
	private String usuarioId;

	@NotNull
	@NotBlank
	private AppPermission permission;

	private String usuarioNome;

	private String usuarioEmail;

	public CollectionUsuarioPermissionDto(String usuarioId, String usuarioNome, String usuarioEmail) {
		this.usuarioId = usuarioId;
		this.usuarioNome = usuarioNome;
		this.usuarioEmail = usuarioEmail;
	}

	@Override
	public String toString() {
		return "CUP DTO: " + this.permission.name();
	}

}
