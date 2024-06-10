package com.keystow.app.controller.form;

import com.keystow.app.model.enuns.CategoriaDeGeradorDeSenha;
import com.keystow.app.model.enuns.TipoDeGeradorDeSenha;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GeradorDeSenhaFormRequestDto {

	@NotNull
	private TipoDeGeradorDeSenha tipoDeGeradorDeSenha;

	@NotNull
	private CategoriaDeGeradorDeSenha categoriaDeGeradorDeSenha;

	private Integer tamanho;

	private Boolean maiusculas;

	private Boolean minusculas;

	private Boolean numeros;

	private Boolean especiais;

	private Integer numerosMinimos;

	private Integer especiaisMinimos;

	private Boolean evitarAmbiguidade;

}
