package com.keystow.app.repository.filter;

import com.keystow.app.model.Dono;
import com.keystow.app.model.Grupo;
import lombok.Data;

import java.util.List;

@Data
public class UsuarioFilterListDto {

	private String id;

	private String usuarioNome;

	private String email;

	private Boolean ativo = null;

	private Dono dono;

	private List<Grupo> grupos;

	private String usuarioAppId;

}
