package com.keystow.app.repository.filter;

import com.keystow.app.model.Dono;
import com.keystow.app.model.Pasta;
import com.keystow.app.model.enuns.TipoDeItemDoCofre;
import lombok.Data;

@Data
public class CredencialFilterListDto {

	private String id;

	private TipoDeItemDoCofre tipoDeItemDoCofre;

	private String nome;

	private Pasta pasta;

	private Dono dono;

	private Boolean lixeira;

}
