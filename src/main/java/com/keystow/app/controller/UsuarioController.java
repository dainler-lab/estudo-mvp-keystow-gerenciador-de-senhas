package com.keystow.app.controller;

import com.keystow.app.controller.form.ExcluirItensSelecionadosRequestAjax;
import com.keystow.app.controller.form.StatusUpdateRequestAjax;
import com.keystow.app.controller.form.UsuarioFormDto;
import com.keystow.app.model.Usuario;
import com.keystow.app.page.PageWrapper;
import com.keystow.app.repository.GrupoRepository;
import com.keystow.app.repository.UsuarioRepository;
import com.keystow.app.repository.filter.UsuarioFilterListDto;
import com.keystow.app.security.AppUser;
import com.keystow.app.service.UsuarioService;
import com.keystow.app.validation.group.ValidationGroupSequence;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping({"/usuario", "/usuarios", "/user", "/users", "/u"})
@AllArgsConstructor
public class UsuarioController {

    public static final String USER_EDIT = "/usuario/usuarioEdit";

    public static final String USER_LIST = "/usuario/usuarioList";

    private final UsuarioRepository usuarioRepository;

    private final GrupoRepository grupoRepository;

    private final UsuarioService usuarioService;

    @GetMapping
    public ModelAndView listFilterUsuarios(UsuarioFilterListDto usuarioFilterListDto, @PageableDefault(size = 3) Pageable pageable,
                                           HttpServletRequest httpServletRequest, @AuthenticationPrincipal AppUser appUser) {

        usuarioFilterListDto.setDono(appUser.getUsuario());
        usuarioFilterListDto.setUsuarioAppId(appUser.getUsuario().getId());

        ModelAndView modelAndView = new ModelAndView(USER_LIST);
        PageWrapper<Usuario> objectPageWrapper = new PageWrapper<>(usuarioRepository.filtro(usuarioFilterListDto, pageable), httpServletRequest);
        modelAndView.addObject("objectPageWrapper", objectPageWrapper);
        modelAndView.addObject("listaDeGruposDonoUsuario", grupoRepository.findAllByDonoId(appUser.getUsuario().getId()));
        return modelAndView;
    }

    @GetMapping("/add")
    public ModelAndView viewFormAddUsuario(UsuarioFormDto usuarioFormDto, @AuthenticationPrincipal AppUser appUser) {
        ModelAndView modelAndView = new ModelAndView(USER_EDIT);
        modelAndView.addObject("usuarioFormDto", usuarioFormDto);
        modelAndView.addObject("listaDeGruposDonoUsuario", grupoRepository.findAllByDonoId(appUser.getUsuario().getId()));
        return modelAndView;
    }

    @PostMapping({"/add", "/{id}"})
    public ModelAndView saveUsuario(@Validated(ValidationGroupSequence.class) UsuarioFormDto usuarioFormDto, BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes, @AuthenticationPrincipal AppUser appUser) {

        if (bindingResult.hasErrors()) {
            return viewFormAddUsuario(usuarioFormDto, appUser);
        }

        usuarioFormDto.setDono(appUser.getUsuario());
        usuarioService.save(usuarioService.toModel(usuarioFormDto));

        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("bi", "check-circle");
        redirectAttributes.addFlashAttribute("message", "Usuário salvo com sucesso ");

        return new ModelAndView("redirect:/u/add");
    }

    @GetMapping("/{id}")
    public ModelAndView editUsuario(@PathVariable("id") String id, @AuthenticationPrincipal AppUser appUser) {
        return viewFormAddUsuario(usuarioService.toDto(usuarioService.getUsuarioById(id)), appUser);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/status", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void atualizarStatus(@RequestBody StatusUpdateRequestAjax statusUpdateRequestAjax) {
        usuarioService.alterarStatusSelecionados(statusUpdateRequestAjax.getIds(), statusUpdateRequestAjax.getStatus());
    }

    @DeleteMapping("/{id}")
    public @ResponseBody ResponseEntity<?> excluirUsuario(@PathVariable("id") String id) {
        usuarioService.excluirSelecionados(new String[]{id});
        return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/all", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void excluirUsuariosSelecionados(@RequestBody ExcluirItensSelecionadosRequestAjax excluirItensSelecionadosRequestAjax) {
        usuarioService.excluirSelecionados(excluirItensSelecionadosRequestAjax.getIds());
    }

}
