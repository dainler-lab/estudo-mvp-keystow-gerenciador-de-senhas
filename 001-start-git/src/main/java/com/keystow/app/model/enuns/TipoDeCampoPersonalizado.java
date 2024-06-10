package com.keystow.app.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoDeCampoPersonalizado {

	TEXTO("Texto"), OCULTADO("Oculto"), BOOLEANO("Booleano");

	private final String nome;

}
