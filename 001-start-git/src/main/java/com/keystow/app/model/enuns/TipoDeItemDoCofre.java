package com.keystow.app.model.enuns;

import lombok.Getter;

@Getter
public enum TipoDeItemDoCofre {

	CREDENCIAL("Credencial"), CARTAO("Cartão"), NOTA("Nota"), IDENTIDADE("Identidade"), OUTRO("Outro");

	private final String descricao;

	TipoDeItemDoCofre(String descricao) {
		this.descricao = descricao;
	}

}
