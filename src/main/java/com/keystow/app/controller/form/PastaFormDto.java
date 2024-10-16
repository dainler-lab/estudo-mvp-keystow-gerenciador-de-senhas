package com.keystow.app.controller.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PastaFormDto {

	private String id;

	@NotNull(message = "O nome é obrigatório")
	@NotBlank(message = "O nome não deve estar vazio ou em branco")
	private String nome;

	public boolean isNew() {
		return this.id == null || this.id.isBlank() || this.id.isEmpty();
	}

}
