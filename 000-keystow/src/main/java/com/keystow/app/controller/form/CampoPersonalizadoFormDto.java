package com.keystow.app.controller.form;

import com.keystow.app.model.enuns.TipoDeCampoPersonalizado;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CampoPersonalizadoFormDto {

	private String id;

	@Enumerated(EnumType.STRING)
	@NotNull
	private TipoDeCampoPersonalizado tipoDeCampoPersonalizado;

	private String nome;

	private String valor;

	private ItemFormDto itemFormDto;

	private String itemFormUUID;

}
