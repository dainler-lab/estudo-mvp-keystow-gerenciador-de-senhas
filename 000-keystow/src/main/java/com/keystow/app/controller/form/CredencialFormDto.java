package com.keystow.app.controller.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CredencialFormDto extends ItemFormDto {

	@NotBlank(message = "O campo nome de usuário não pode ser vazio")
	@NotNull(message = "O campo nome de usuário é obrigatório")
	private String nomeDeUsuario;

	@NotBlank(message = "O campo senha não pode ser vazio")
	@NotNull(message = "O campo de senha é obrigatório")
	private String senha;

	private List<UriFormDto> uris = new ArrayList<>();

	public void setUris(List<UriFormDto> uris) {
		this.uris = uris;
		this.uris.forEach(uri -> uri.setCredencialFormDto(this));
	}

	@Override
	public String toString() {
		return "CredencialFormDto: " + getId();
	}

}
