package com.keystow.app.model.enuns;

import lombok.Getter;

@Getter
public enum CategoriaDeGeradorDeSenha {

	CATEGORIA_SENHA("Senha"), FRASE_SECRETA("Frase Secreta");

	private final String descricao;

	CategoriaDeGeradorDeSenha(String descricao) {
		this.descricao = descricao;
	}

}
