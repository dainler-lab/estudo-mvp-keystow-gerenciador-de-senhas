package com.keystow.app.controller.form;

import lombok.Data;

import java.util.Collection;

@Data
public class ItensFavoritosUpdateRequestAjax {

	private Collection<String> ids;

	private String action;

	private Boolean favorito;

	public Boolean updateFavorito() {
		if ("ATIVAR".equals(this.action)) {
			this.favorito = true;
		}
		else if ("DESATIVAR".equals(this.action)) {
			this.favorito = false;
		}
		return this.favorito;
	}

}
