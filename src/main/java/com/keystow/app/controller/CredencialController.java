package com.keystow.app.controller;

import com.keystow.app.controller.form.*;
import com.keystow.app.model.Credencial;
import com.keystow.app.model.enuns.TipoDeCampoPersonalizado;
import com.keystow.app.model.enuns.TipoDeItemDoCofre;
import com.keystow.app.page.PageWrapper;
import com.keystow.app.repository.CredencialRepository;
import com.keystow.app.repository.PastaRepository;
import com.keystow.app.repository.filter.CredencialFilterListDto;
import com.keystow.app.security.AppUser;
import com.keystow.app.service.CredencialService;
import com.keystow.app.session.campopersonalizado.ListaDeListasDeItensDeCamposPersonalizadosSession;
import com.keystow.app.session.uri.ListaDeListasDeItensUrisFormDtoSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping({"/credencial", "/credenciais"})
@AllArgsConstructor
public class CredencialController {

    public static final String CREDENCIAL_EDIT = "/credencial/credencialEdit";

    public static final String CREDENCIAL_LIST = "/credencial/credencialList";

    private final PastaRepository pastaRepository;

    private final CredencialRepository credencialRepository;

    private final CredencialService credencialService;

    private final ModelMapper modelMapper;

    private final ListaDeListasDeItensDeCamposPersonalizadosSession listaDeListasDeItensCpSession;

    private final ListaDeListasDeItensUrisFormDtoSession listaDeListasDeItensUrisFormDtoSession;

    @GetMapping
    public ModelAndView listItemCredenciais(CredencialFilterListDto credencialFilterListDto, @PageableDefault(size = 3) Pageable pageable,
                                            HttpServletRequest httpServletRequest, @AuthenticationPrincipal AppUser appUser) {

        credencialFilterListDto.setDono(appUser.getUsuario());
        PageWrapper<Credencial> objectPageWrapper = new PageWrapper<>(credencialRepository.filtro(credencialFilterListDto, pageable), httpServletRequest);
        ModelAndView modelAndView = new ModelAndView(CREDENCIAL_LIST);
        modelAndView.addObject("listaDeTiposDeItens", TipoDeItemDoCofre.values());
        modelAndView.addObject("listaDePastasDonoUsuario", pastaRepository.findAllByDonoId(appUser.getUsuario().getId()));
        modelAndView.addObject("objectPageWrapper", objectPageWrapper);
        return modelAndView;
    }

    @GetMapping("/add")
    public ModelAndView viewFormAddItemCredencial(CredencialFormDto credencialFormDto, @AuthenticationPrincipal AppUser appUser) {

        ModelAndView modelAndView = new ModelAndView(CREDENCIAL_EDIT);

        setUuid(credencialFormDto);

        modelAndView.addObject("uris", credencialFormDto.getUris());
        modelAndView.addObject("itens", credencialFormDto.getCamposPersonalizadosFormDto());

        modelAndView.addObject("listDeTiposDeItens", TipoDeItemDoCofre.values());
        modelAndView.addObject("listaDePastasDonoUsuario", pastaRepository.findAllByDonoId(appUser.getUsuario().getId()));
        modelAndView.addObject("listaDeTiposDeCamposPersonalizados", TipoDeCampoPersonalizado.values());
        modelAndView.addObject("credencialFormDto", credencialFormDto);
        return modelAndView;
    }

    @PostMapping({"/add", "/{id}"})
    public ModelAndView saveItemCredencial(@Valid CredencialFormDto credencialFormDto, BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                           @AuthenticationPrincipal AppUser appUser) {

        addListasDeItensParaFormDto(credencialFormDto);

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = viewFormAddItemCredencial(credencialFormDto, appUser);
            List<CampoPersonalizadoFormDto> itens =
                    listaDeListasDeItensCpSession.getListaDeItensDeCamposPersonalizadosDto(credencialFormDto.getItemFormUUID());
            modelAndView.addObject("itens", itens);
            List<UriFormDto> uris = listaDeListasDeItensUrisFormDtoSession.getListUriFormDtosPorUUID(credencialFormDto.getItemFormUUID());
            modelAndView.addObject("uris", uris);
            return modelAndView;
        }

        credencialFormDto.setDono(appUser.getUsuario()); // VERIFICAR SE É NECESSÁRIO
        credencialService.save(credencialService.toModel(credencialFormDto));

        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "Credencial salva com sucesso ");
        redirectAttributes.addFlashAttribute("bi", "check-circle");

        return new ModelAndView("redirect:/credencial/add");
    }

    @GetMapping("/{id}")
    public ModelAndView editItemCredencial(@PathVariable("id") String id, @AuthenticationPrincipal AppUser appUser) {
        CredencialFormDto credencialFormDto = credencialService.toDto(credencialRepository.getCredencialComCamposPersonalizados(id));
        setUuid(credencialFormDto);
        addListasDeItensParaSession(credencialFormDto);
        return viewFormAddItemCredencial(credencialFormDto, appUser);
    }

    // ---------------- MÉTODOS PARA MANIPULAR ACTIONS AJAX ----------------\\
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/favoritos", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void atualizarItensSelecionadosFavoritos(@RequestBody ItensFavoritosUpdateRequestAjax itensFavoritosUpdateRequestAjax) {
        credencialService.alterarFavoritosSelecionados(itensFavoritosUpdateRequestAjax.getIds(), itensFavoritosUpdateRequestAjax.updateFavorito());
    }

    @DeleteMapping("/{id}")
    public @ResponseBody ResponseEntity<?> excluirItemParaLixeira(@PathVariable("id") String id) {
        credencialService.excluirItensSelecionadosParaLixeira(new String[]{id});
        return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/all", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void excluirItensSelecionadosParaLixeira(@RequestBody ExcluirItensSelecionadosRequestAjax excluirItensSelecionadosRequestAjax) {
        credencialService.excluirItensSelecionadosParaLixeira(excluirItensSelecionadosRequestAjax.getIds());
    }

    // ---------------- MÉTODOS PARA MANIPULAR ITENS URIS ----------------\\
    private ModelAndView mvListaDeUris(String uuid) {
        ModelAndView modelAndView = new ModelAndView("credencial/listaDeUris");
        modelAndView.addObject("uris", listaDeListasDeItensUrisFormDtoSession.getListUriFormDtosPorUUID(uuid));
        return modelAndView;
    }

    @PostMapping(value = "/uri", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ModelAndView createItemUri(@RequestBody UriFormDto uriFormDto) {
        listaDeListasDeItensUrisFormDtoSession.addItemUriListaDeListas(uriFormDto.getItemFormUUID(), uriFormDto);
        return mvListaDeUris(uriFormDto.getItemFormUUID());
    }

    @PutMapping(value = "/uri", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ModelAndView updateItemUri(@RequestBody UriFormDto uriFormDto) {
        listaDeListasDeItensUrisFormDtoSession.alterarItemUriListaDeListas(uriFormDto.getItemFormUUID(), uriFormDto);
        return mvListaDeUris(uriFormDto.getItemFormUUID());
    }

    @DeleteMapping(value = "/uri/{uuid}/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ModelAndView deleteItemUri(@PathVariable("id") String id, @PathVariable("uuid") String uuid) {
        listaDeListasDeItensUrisFormDtoSession.excluirItemUriListaDeListas(uuid, id);
        return mvListaDeUris(uuid);
    }

    // ---------------- MÉTODOS PARA MANIPULAR ITENS DE CAMPOS ----------------\\
    private ModelAndView mvListaDeCamposPersonalizados(String uuid) {
        ModelAndView modelAndView = new ModelAndView("credencial/listaDeItensDeCamposPersonalizados");
        modelAndView.addObject("itens", listaDeListasDeItensCpSession.getListaDeItensDeCamposPersonalizadosDto(uuid));
        return modelAndView;
    }

    @PostMapping(value = "/item-campo-personalizado", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ModelAndView createItemDeCampoPersonalizado(@RequestBody CampoPersonalizadoFormDto campoPersonalizadoFormDto) {
        listaDeListasDeItensCpSession.addItemCampoPersonalizado(campoPersonalizadoFormDto.getItemFormUUID(),
                                                                modelMapper.map(campoPersonalizadoFormDto, CampoPersonalizadoFormDto.class));
        return mvListaDeCamposPersonalizados(campoPersonalizadoFormDto.getItemFormUUID());
    }

    @PutMapping(value = "/item-campo-personalizado", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ModelAndView updateItemCampoDePersonalizado(@RequestBody CampoPersonalizadoFormDto campoPersonalizadoFormDto) {
        listaDeListasDeItensCpSession.alterarItemCampoPersonalizado(campoPersonalizadoFormDto.getItemFormUUID(), campoPersonalizadoFormDto);
        return mvListaDeCamposPersonalizados(campoPersonalizadoFormDto.getItemFormUUID());
    }

    @DeleteMapping(value = "/item-campo-personalizado/{uuid}/{id}")
    public ModelAndView deleteItemCampoDePersonalizado(@PathVariable("id") String id, @PathVariable("uuid") String uuid) {
        listaDeListasDeItensCpSession.excluirItemCampoPersonalizado(uuid, id);
        return mvListaDeCamposPersonalizados(uuid);
    }

    // ---------------- UTIL ----------------\\
    private void setUuid(CredencialFormDto credencialFormDto) {
        if (!StringUtils.hasText(credencialFormDto.getItemFormUUID())) {
            credencialFormDto.setItemFormUUID(UUID.randomUUID().toString());
        }
    }

    private void addListasDeItensParaFormDto(CredencialFormDto credencialFormDto) {
        credencialFormDto.setUris(listaDeListasDeItensUrisFormDtoSession.getListUriFormDtosPorUUID(credencialFormDto.getItemFormUUID()));

        credencialFormDto.setCamposPersonalizadosItensFormDtos(
                listaDeListasDeItensCpSession.getListaDeItensDeCamposPersonalizadosDto(credencialFormDto.getItemFormUUID()));
    }

    private void addListasDeItensParaSession(CredencialFormDto credencialFormDto) {
        for (CampoPersonalizadoFormDto item : credencialFormDto.getCamposPersonalizadosFormDto()) {
            item.setItemFormUUID(credencialFormDto.getItemFormUUID());
            listaDeListasDeItensCpSession.addItemCampoPersonalizado(credencialFormDto.getItemFormUUID(), item);
        }

        for (UriFormDto uri : credencialFormDto.getUris()) {
            uri.setItemFormUUID(credencialFormDto.getItemFormUUID());
            listaDeListasDeItensUrisFormDtoSession.addItemUriListaDeListas(credencialFormDto.getItemFormUUID(), uri);
        }
    }

}
