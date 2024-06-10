package com.keystow.app.controller.list;

import com.keystow.app.model.Uri;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CredencialListAppHomeDto {

	private String id;

	private String nome;

	private String nomeDeUsuario;

	private String senha;

	private Boolean favorito;

	private List<Uri> uris = new ArrayList<>();

	@Override
	public String toString() {
		return "ITEM LIST APP HOME DTO: " + this.nome;
	}

}
