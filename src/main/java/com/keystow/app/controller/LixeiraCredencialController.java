package com.keystow.app.controller;

import com.keystow.app.model.Credencial;
import com.keystow.app.model.enuns.TipoDeItemDoCofre;
import com.keystow.app.page.PageWrapper;
import com.keystow.app.repository.CredencialRepository;
import com.keystow.app.repository.filter.CredencialFilterListDto;
import com.keystow.app.security.AppUser;
import com.keystow.app.service.CredencialService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping({"/lixeira", "/l"})
@AllArgsConstructor
public class LixeiraCredencialController {

    public static final String CREDENCIAL_LIST_LIXEIRA = "/credencial/credencialLixeira";

    private final CredencialRepository credencialRepository;

    private final CredencialService credencialService;

    @GetMapping
    public ModelAndView listItemCredenciais(CredencialFilterListDto credencialFilterListDto, @PageableDefault(size = 3) Pageable pageable,
                                            HttpServletRequest httpServletRequest, @AuthenticationPrincipal AppUser appUser) {

        ModelAndView modelAndView = new ModelAndView(CREDENCIAL_LIST_LIXEIRA);

        credencialFilterListDto.setDono(appUser.getUsuario());

        PageWrapper<Credencial> objectPageWrapper =
                new PageWrapper<>(credencialRepository.filtroDonoAndLixeira(credencialFilterListDto, pageable), httpServletRequest);

        modelAndView.addObject("listaDeTiposDeItens", TipoDeItemDoCofre.values());
        modelAndView.addObject("objectPageWrapper", objectPageWrapper);
        return modelAndView;
    }

    @PutMapping("/restaurar")
    public ResponseEntity<Void> restaurarSelecionados(@RequestBody List<String> ids, @AuthenticationPrincipal AppUser appUser) {
        credencialService.restaurarSelecionadosLixeira(ids, appUser.getUsuario().getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/restaurar/all")
    public ResponseEntity<Void> restaurarTodosAll(@AuthenticationPrincipal AppUser appUser) {
        credencialService.restaurarTodosAllLixeira(appUser.getUsuario().getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public @ResponseBody ResponseEntity<Void> excluirItemDaLixeiraPermanentemente(@PathVariable("id") String id,
                                                                                  @AuthenticationPrincipal AppUser appUser) {
        credencialService.excluirSelecionadosPermanentemente(List.of(id), appUser.getUsuario().getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remover")
    public ResponseEntity<Void> excluirSelecionadosDaLixeiraPermanentemente(@RequestBody List<String> ids, @AuthenticationPrincipal AppUser appUser) {
        credencialService.excluirSelecionadosPermanentemente(ids, appUser.getUsuario().getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remover/all")
    public ResponseEntity<Void> esvaziarTodosDaLixeiraPermanentemente(@AuthenticationPrincipal AppUser appUser) {
        credencialService.esvaziarLixeiraPermanentemente(appUser.getUsuario().getId());
        return ResponseEntity.noContent().build();
    }

}
