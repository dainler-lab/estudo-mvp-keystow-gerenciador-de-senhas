package com.keystow.app.controller;

import com.keystow.app.controller.form.PastaFormDto;
import com.keystow.app.exception.ImpossivelExcluirEntidadeForeignKeyConstraintException;
import com.keystow.app.exception.NomeJaExisteException;
import com.keystow.app.model.Pasta;
import com.keystow.app.page.PageWrapper;
import com.keystow.app.repository.PastaRepository;
import com.keystow.app.repository.filter.PastaFilterListDto;
import com.keystow.app.security.AppUser;
import com.keystow.app.service.PastaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/pasta")
@AllArgsConstructor
public class PastaController {

	public static final String PASTA_LIST = "/pasta/pastaList";

	public static final String PASTA_EDIT = "/pasta/pastaEdit";

	private final PastaRepository pastaRepository;

	private final PastaService pastaService;

	@GetMapping
	public ModelAndView listFilterPastas(PastaFilterListDto pastaFilterListDto,
			@PageableDefault(size = 2) Pageable pageable, HttpServletRequest httpServletRequest,
			@AuthenticationPrincipal AppUser appUser) {

		pastaFilterListDto.setDono(appUser.getUsuario());

		ModelAndView modelAndView = new ModelAndView(PASTA_LIST);
		modelAndView.addObject("listaDePastasDonoUsuario",
				pastaRepository.findAllByDonoId(appUser.getUsuario().getId()));

		PageWrapper<Pasta> objectPageWrapper = new PageWrapper<>(pastaRepository.filtro(pastaFilterListDto, pageable),
				httpServletRequest);
		modelAndView.addObject("objectPageWrapper", objectPageWrapper);
		return modelAndView;
	}

	@GetMapping("/add")
	public ModelAndView viewFormAddPasta(PastaFormDto pastaFormDto) {
		ModelAndView modelAndView = new ModelAndView(PASTA_EDIT);
		modelAndView.addObject("pastaFormDto", pastaFormDto);
		return modelAndView;
	}

	@PostMapping(value = { "/add", "/{id}" })
	public ModelAndView savePasta(@Valid PastaFormDto pastaFormDto, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, @AuthenticationPrincipal AppUser appUser) {

		if (bindingResult.hasErrors()) {
			return viewFormAddPasta(pastaFormDto);
		}

		try {
			pastaService.salvarPastaPorDono(pastaFormDto, appUser.getUsuario().getId());
		}
		catch (NomeJaExisteException exception) {
			bindingResult.rejectValue("nome", exception.getMessage(), exception.getMessage());
			return viewFormAddPasta(pastaFormDto);
		}

		redirectAttributes.addFlashAttribute("type", "success");
		redirectAttributes.addFlashAttribute("message", "Pasta salva com sucesso");
		redirectAttributes.addFlashAttribute("bi", "check-circle");
		return new ModelAndView("redirect:/pasta/add");
	}

	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody ResponseEntity<?> savePastaModalAjax(@RequestBody @Valid PastaFormDto pastaFormDto,
			BindingResult bindingResult, @AuthenticationPrincipal AppUser appUser) {
		PastaFormDto pastaFormDtoResponse;

		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest()
				.body(Objects.requireNonNull(bindingResult.getFieldError("nome")).getDefaultMessage());
		}

		try {
			pastaFormDtoResponse = pastaService.salvarPastaPorDono(pastaFormDto, appUser.getUsuario().getId());
		}
		catch (NomeJaExisteException exception) {
			return ResponseEntity.badRequest().body(exception.getMessage());
		}
		return ResponseEntity.ok(pastaFormDtoResponse);
	}

	@GetMapping("/{id}")
	public ModelAndView editPasta(@PathVariable("id") String id) {
		return viewFormAddPasta(pastaService.getPastaById(id));
	}

	@DeleteMapping("/{id}")
	public @ResponseBody ResponseEntity<?> removePasta(@PathVariable("id") String id,
			@AuthenticationPrincipal AppUser appUser) {
		try {
			pastaService.excluirPastaPorDono(id, appUser.getUsuario().getId());
		}
		catch (ImpossivelExcluirEntidadeForeignKeyConstraintException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
			// return
			// ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}

}
