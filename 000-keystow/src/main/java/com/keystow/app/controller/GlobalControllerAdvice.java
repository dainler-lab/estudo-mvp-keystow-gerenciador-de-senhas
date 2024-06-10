package com.keystow.app.controller;

import com.keystow.app.model.enuns.AppPermission;
import com.keystow.app.model.enuns.CategoriaDeGeradorDeSenha;
import com.keystow.app.model.enuns.TipoDeGeradorDeSenha;
import com.keystow.app.service.enuns.StatusUsuario;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

	@ModelAttribute("permissions")
	public List<AppPermission> permissions() {
		return List.of(AppPermission.values());
	}

	@ModelAttribute("tiposDoGerador")
	public List<TipoDeGeradorDeSenha> tiposDoGerador() {
		return List.of(TipoDeGeradorDeSenha.values());
	}

	@ModelAttribute("categoriasDoGerador")
	public List<CategoriaDeGeradorDeSenha> categoriasDoGerador() {
		return List.of(CategoriaDeGeradorDeSenha.values());
	}

	@ModelAttribute("statusDoUsuario")
	public List<StatusUsuario> statusDoUsuario() {
		return List.of(StatusUsuario.values());
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(false);
		binder.registerCustomEditor(String.class, stringTrimmerEditor);
	}

}
