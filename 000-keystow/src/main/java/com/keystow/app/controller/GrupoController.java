package com.keystow.app.controller;

import com.keystow.app.controller.form.GrupoFormDto;
import com.keystow.app.controller.list.GrupoUsuarioPesquisaDto;
import com.keystow.app.exception.EntityNotFoundBusinessException;
import com.keystow.app.exception.ImpossivelExcluirEntidadeForeignKeyConstraintException;
import com.keystow.app.exception.NomeJaExisteException;
import com.keystow.app.model.Grupo;
import com.keystow.app.page.PageWrapper;
import com.keystow.app.repository.GrupoRepository;
import com.keystow.app.repository.UsuarioRepository;
import com.keystow.app.repository.filter.GrupoFilterListDto;
import com.keystow.app.security.AppUser;
import com.keystow.app.service.GrupoService;
import com.keystow.app.session.grupo.usuarios.ListasDeGrupoUsuariosDtoSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping({"/grupo", "/grupos", "/group", "/groups", "/g"})
@AllArgsConstructor
public class GrupoController {

    public static final String GRUPO_LIST = "/grupo/grupoList";

    public static final String GRUPO_EDIT = "/grupo/grupoEdit";

    private final GrupoRepository grupoRepository;

    private final UsuarioRepository usuarioRepository;

    private final GrupoService grupoService;

    private final ListasDeGrupoUsuariosDtoSession listasDeGrupoUsuariosDtoSession;

    @GetMapping
    public ModelAndView listFilterGrupos(GrupoFilterListDto grupoFilterListDto, @PageableDefault(size = 3) Pageable pageable,
                                         HttpServletRequest httpServletRequest, @AuthenticationPrincipal AppUser appUser) {
        ModelAndView modelAndView = new ModelAndView(GRUPO_LIST);
        grupoFilterListDto.setDono(appUser.getUsuario());
        PageWrapper<Grupo> objectPageWrapper = new PageWrapper<>(grupoRepository.filtro(grupoFilterListDto, pageable), httpServletRequest);
        modelAndView.addObject("objectPageWrapper", objectPageWrapper);
        return modelAndView;
    }

    @GetMapping("/add")
    public ModelAndView viewFormGrupo(GrupoFormDto grupoFormDto) {
        setUuidGrupoFormDto(grupoFormDto);
        ModelAndView modelAndView = new ModelAndView(GRUPO_EDIT);
        modelAndView.addObject("grupoWithUsuarios", listasDeGrupoUsuariosDtoSession.getListGupDtoSessionByUUID(grupoFormDto.getSessionUUID()));
        modelAndView.addObject("grupoFormDto", grupoFormDto);
        return modelAndView;
    }

    @PostMapping(value = {"/add", "/{id}"})
    public ModelAndView saveGrupo(@Valid GrupoFormDto grupoFormDto, BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                  @AuthenticationPrincipal AppUser appUser) {

        addListaSessionParaDto(grupoFormDto);

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = viewFormGrupo(grupoFormDto);
            List<GrupoUsuarioPesquisaDto> gupDtoList = listasDeGrupoUsuariosDtoSession.getListGupDtoSessionByUUID(grupoFormDto.getSessionUUID());
            modelAndView.addObject("grupoWithUsuarios", gupDtoList);
            return modelAndView;
        }

        try {
            grupoService.salvarGrupoPorDono(grupoFormDto, appUser.getUsuario().getId());
        } catch (NomeJaExisteException exception) {
            bindingResult.rejectValue("nome", exception.getMessage(), exception.getMessage());
            return viewFormGrupo(grupoFormDto);
        }
        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "Grupo salvo com sucesso");
        redirectAttributes.addFlashAttribute("bi", "check-circle");
        return new ModelAndView("redirect:/grupo/add");
    }

    @GetMapping("/{id}")
    public ModelAndView editGrupo(@PathVariable("id") String id, @AuthenticationPrincipal AppUser appUser) {
        // @formatter:off
        Grupo grupo = grupoRepository.findByIdAndDonoIdWithUsuarios(id, appUser.getUsuario().getId())
                                     .orElseThrow(() -> new EntityNotFoundBusinessException("Grupo não encontrado"));
        // @formatter:on
        GrupoFormDto grupoFormDto = grupoService.toDto(grupo);
        setUuidGrupoFormDto(grupoFormDto);
        addListaDtoParaSession(grupoFormDto);
        return viewFormGrupo(grupoFormDto);
    }

    @DeleteMapping("/{id}")
    public @ResponseBody ResponseEntity<?> removeGrupo(@PathVariable("id") String id, @AuthenticationPrincipal AppUser appUser) {
        try {
            grupoService.excluirGrupoPorDono(id, appUser.getUsuario().getId());
        } catch (ImpossivelExcluirEntidadeForeignKeyConstraintException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            String message = "Não é possível excluir o grupo, pois existem itens, usuários ou coleções vinculados a ele";
            return ResponseEntity.unprocessableEntity().body(message);
        }
        return ResponseEntity.ok().build();
    }

    // ---------------- TABELA USUARIOS SESSION ----------------\\
    @GetMapping(value = "/usuarios")
    public @ResponseBody List<GrupoUsuarioPesquisaDto> pesquisarUserParaTabelaGupSessionDto(String nomeOuEmail) {
        return usuarioRepository.findByNomeOrEmailStartingWithIgnoreCase(nomeOuEmail);
    }

    private ModelAndView mvGupDtoSession(String uuid) {
        ModelAndView modelAndView = new ModelAndView("grupo/listaDeUsuariosByGrupoSession");
        modelAndView.addObject("grupoWithUsuarios", listasDeGrupoUsuariosDtoSession.getListGupDtoSessionByUUID(uuid));
        return modelAndView;
    }

    @PostMapping("/usuarios")
    public ModelAndView adicionarGupSession(@RequestBody GrupoUsuarioPesquisaDto grupoUsuarioPesquisaDto) {
        listasDeGrupoUsuariosDtoSession.adicionar(grupoUsuarioPesquisaDto.getSessionUUID(), grupoUsuarioPesquisaDto);
        return mvGupDtoSession(grupoUsuarioPesquisaDto.getSessionUUID());
    }

    @DeleteMapping("/usuarios")
    public ModelAndView excluirGupSession(@RequestBody GrupoUsuarioPesquisaDto grupoUsuarioPesquisaDto) {
        listasDeGrupoUsuariosDtoSession.excluir(grupoUsuarioPesquisaDto);
        return mvGupDtoSession(grupoUsuarioPesquisaDto.getSessionUUID());
    }

    // ---------------- UTIL ----------------\\
    private void addListaDtoParaSession(GrupoFormDto grupoFormDto) {
        grupoFormDto.getUsuarios().forEach(gupDto -> listasDeGrupoUsuariosDtoSession.adicionar(grupoFormDto.getSessionUUID(), gupDto));
    }

    private void addListaSessionParaDto(GrupoFormDto grupoFormDto) {
        grupoFormDto.setUsuarios(listasDeGrupoUsuariosDtoSession.getListGupDtoSessionByUUID(grupoFormDto.getSessionUUID()));
    }

    private void setUuidGrupoFormDto(GrupoFormDto grupoFormDto) {
        if (grupoFormDto.getSessionUUID() == null || grupoFormDto.getSessionUUID().isBlank()) {
            grupoFormDto.setSessionUUID(java.util.UUID.randomUUID().toString());
        }
    }

}
