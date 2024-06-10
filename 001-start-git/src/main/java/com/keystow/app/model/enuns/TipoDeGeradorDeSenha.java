package com.keystow.app.model.enuns;

import lombok.Getter;

@Getter
public enum TipoDeGeradorDeSenha {

	GERADOR_SENHA("Senha"), NOME_USUARIO("Nome de Usuário");

	private final String descricao;

	TipoDeGeradorDeSenha(String descricao) {
		this.descricao = descricao;
	}

}
