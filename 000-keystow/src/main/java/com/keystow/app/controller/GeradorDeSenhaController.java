package com.keystow.app.controller;

import com.keystow.app.controller.form.GeradorDeSenhaFormRequestDto;
import com.keystow.app.model.SenhasCollection;
import com.keystow.app.security.AppUser;
import com.keystow.app.service.GeradorDeSenhaService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/senha")
@AllArgsConstructor
public class GeradorDeSenhaController {

	private final GeradorDeSenhaService geradorDeSenhaService;

	@PostMapping(value = "/generate", consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody ResponseEntity<?> gerarSenhaPorDono(
			@RequestBody GeradorDeSenhaFormRequestDto geradorDeSenhaFormRequestDto,
			@AuthenticationPrincipal AppUser appUser) {
		String senha = geradorDeSenhaService.gerarSenhaPorUsuario(geradorDeSenhaFormRequestDto,
				appUser.getUsuario().getId());
		return ResponseEntity.ok(senha);
	}

	@GetMapping("/history")
	public ModelAndView listarSenhasPorDono(@AuthenticationPrincipal AppUser appUser) {
		ModelAndView modelAndView = new ModelAndView("app/historico-senha");
		List<SenhasCollection> senhasCollections = geradorDeSenhaService
			.buscarSenhasPorCriadoEmDescAndUsuario(appUser.getUsuario().getId());
		// Formatar a data para cada senha
		for (SenhasCollection senhasCollection : senhasCollections) {
			if (senhasCollection != null) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy HH:mm:ss",
						new Locale("pt", "BR"));
				String formattedDate = senhasCollection.getCriadoEm().format(formatter);
				senhasCollection.setCriadoEmFormattedDate(formattedDate);
			}
		}
		modelAndView.addObject("senhas", senhasCollections);
		return modelAndView;
	}

	@DeleteMapping("/history")
	public @ResponseBody ResponseEntity<?> deletarSenhasPorDono(@AuthenticationPrincipal AppUser appUser) {
		geradorDeSenhaService.deletarTodasSenhasPorUsuario(appUser.getUsuario().getId());
		return ResponseEntity.ok().build();
	}

}
