package com.keystow.app.controller;

import com.keystow.app.controller.form.CollectionFormDto;
import com.keystow.app.controller.list.CollectionGrupoPermissionDto;
import com.keystow.app.controller.list.CollectionUsuarioPermissionDto;
import com.keystow.app.controller.list.ItemCollectionPesquisaDto;
import com.keystow.app.exception.ImpossivelExcluirEntidadeForeignKeyConstraintException;
import com.keystow.app.exception.NomeJaExisteException;
import com.keystow.app.model.ItemDoCofre;
import com.keystow.app.model.collection.CollectionDeItensDoCofre;
import com.keystow.app.model.collection.CollectionGrupoPermission;
import com.keystow.app.model.collection.CollectionUsuarioPermission;
import com.keystow.app.model.enuns.AppPermission;
import com.keystow.app.page.PageWrapper;
import com.keystow.app.repository.CollectionRepository;
import com.keystow.app.repository.GrupoRepository;
import com.keystow.app.repository.ItemRepository;
import com.keystow.app.repository.UsuarioRepository;
import com.keystow.app.repository.filter.CollectionFilterListDto;
import com.keystow.app.security.AppUser;
import com.keystow.app.service.CollectionService;
import com.keystow.app.session.collection.cgp.ListasDeCgpDtoSession;
import com.keystow.app.session.collection.cup.ListasDeCupDtoSession;
import com.keystow.app.session.collection.itens.ListasDeCollectionItensDtoSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
@RequestMapping({"/collection", "/collections", "/colecao", "/colecoes", "/c"})
@AllArgsConstructor
public class CollectionController {

    public static final String COLLECTION_LIST = "/collection/collectionList";

    public static final String COLLECTION_EDIT = "/collection/collectionEdit";

    private final CollectionRepository collectionRepository;

    private final UsuarioRepository usuarioRepository;

    private final GrupoRepository grupoRepository;

    private final ItemRepository itemRepository;

    private final CollectionService collectionService;

    private final ListasDeCupDtoSession listasDeCupDtoSession;

    private final ListasDeCgpDtoSession listasDeCgpDtoSession;

    private final ListasDeCollectionItensDtoSession listasDeCollectionItensDtoSession;

    @GetMapping
    public ModelAndView listFilterCollections(CollectionFilterListDto collectionFilterListDto, @PageableDefault(size = 3) Pageable pageable,
                                              HttpServletRequest httpServletRequest, @AuthenticationPrincipal AppUser appUser) {

        ModelAndView modelAndView = new ModelAndView(COLLECTION_LIST);
        collectionFilterListDto.setDono(appUser.getUsuario());
        PageWrapper<CollectionDeItensDoCofre> objectPageWrapper =
                new PageWrapper<>(collectionRepository.filtro(collectionFilterListDto, pageable), httpServletRequest);
        modelAndView.addObject("objectPageWrapper", objectPageWrapper);
        return modelAndView;
    }

    @GetMapping("/add")
    public ModelAndView viewFormCollection(CollectionFormDto collectionFormDto) {
        setUuidCollectionFormDto(collectionFormDto);
        ModelAndView modelAndView = new ModelAndView(COLLECTION_EDIT);
        modelAndView.addObject("collectionUsuarioPermissionDtoList", collectionFormDto.getCollectionsUsuariosPermissionsDtoList());
        modelAndView.addObject("collectionGrupoPermissionDtoList", collectionFormDto.getCollectionsGruposPermissionsDtoList());
        modelAndView.addObject("itensDtoList", listasDeCollectionItensDtoSession.getListItensDtoSessionPorUUID(collectionFormDto.getSessionUUID()));
        modelAndView.addObject("collectionFormDto", collectionFormDto);
        return modelAndView;
    }

    @PostMapping(value = {"/add", "/{id}"})
    public ModelAndView saveCollection(@Valid CollectionFormDto collectionFormDto, BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                       @AuthenticationPrincipal AppUser appUser) {

        addListasSessionParaFormDto(collectionFormDto);

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = viewFormCollection(collectionFormDto);

            List<CollectionUsuarioPermissionDto> cupDtoList = listasDeCupDtoSession.getListCupSessionDtoPorUUID(collectionFormDto.getSessionUUID());
            modelAndView.addObject("collectionUsuarioPermissionDtoList", cupDtoList);

            List<CollectionGrupoPermissionDto> cgpDtoList = listasDeCgpDtoSession.getListCgpDtoSessionPorUUID(collectionFormDto.getSessionUUID());
            modelAndView.addObject("collectionGrupoPermissionDtoList", cgpDtoList);

            List<ItemCollectionPesquisaDto> itensDtoList =
                    listasDeCollectionItensDtoSession.getListItensDtoSessionPorUUID(collectionFormDto.getSessionUUID());
            modelAndView.addObject("itensDtoList", itensDtoList);

            return modelAndView;
        }

        try {
            collectionService.salvarCollectionPorDono(collectionFormDto, appUser.getUsuario().getId());
        } catch (NomeJaExisteException exception) {
            bindingResult.rejectValue("nome", exception.getMessage(), exception.getMessage());
            return viewFormCollection(collectionFormDto);
        }
        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "Coleção salva com sucesso");
        redirectAttributes.addFlashAttribute("bi", "check-circle");
        return new ModelAndView("redirect:/collection/add");
    }

    @GetMapping("/{id}")
    public ModelAndView editCollection(@PathVariable("id") String id, @AuthenticationPrincipal AppUser appUser) {

        CollectionDeItensDoCofre collection = collectionService.getCollectionByIdAndDonoId(id, appUser.getUsuario().getId());

        List<CollectionUsuarioPermission> cupByIdAndDonoIdList =
                collectionRepository.getCollectionUsuarioPermissionsByIdAndDonoId(id, appUser.getUsuario().getId());
        collection.addCollectionsUsuariosPermissionsModelList(cupByIdAndDonoIdList);

        List<CollectionGrupoPermission> cgpByIdAndDonoIdList =
                collectionRepository.getCollectionGrupoPermissionsByIdAndDonoId(id, appUser.getUsuario().getId());
        collection.addCollectionsGrupoPermissionsModelList(cgpByIdAndDonoIdList);

        List<ItemDoCofre> itensByIdAndDonoIdList = collectionRepository.getItensByIdAndDonoId(id, appUser.getUsuario().getId());
        collection.addItensModelList(itensByIdAndDonoIdList);

        CollectionFormDto collectionFormDto = collectionService.toFormDto(collection);
        setUuidCollectionFormDto(collectionFormDto);
        addListasFormDtoParaSession(collectionFormDto);
        return viewFormCollection(collectionFormDto);
    }

    @DeleteMapping("/{id}")
    public @ResponseBody ResponseEntity<?> removeCollection(@PathVariable("id") String id, @AuthenticationPrincipal AppUser appUser) {
        try {
            collectionService.excluirCollectionPorDono(id, appUser.getUsuario().getId());
        } catch (ImpossivelExcluirEntidadeForeignKeyConstraintException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    // ---------------- TABELA CUP SESSION ----------------\\
    @GetMapping(value = "/cup")
    public @ResponseBody List<CollectionUsuarioPermissionDto> pesquisarUserParaTabelaCupSessionDto(String nomeOuEmail,
                                                                                                   @AuthenticationPrincipal AppUser appUser) {
        return usuarioRepository.findByNomeOrEmailStartingWithIgnoreCaseAndNotUsuarioId(nomeOuEmail, appUser.getUsuario().getId());
    }

    private ModelAndView mvTabelaCupSession(String uuid) {
        ModelAndView modelAndView = new ModelAndView("collection/tabelaCupSession");
        modelAndView.addObject("collectionUsuarioPermissionDtoList", listasDeCupDtoSession.getListCupSessionDtoPorUUID(uuid));
        return modelAndView;
    }

    @PostMapping("/cup")
    public ModelAndView adicionarCupSession(@RequestBody CollectionUsuarioPermissionDto collectionUsuarioPermissionDto) {
        collectionUsuarioPermissionDto.setPermission(AppPermission.PODE_VER_EXCETO_SENHAS);
        listasDeCupDtoSession.adicionarCupSessionDto(collectionUsuarioPermissionDto.getSessionUUID(), collectionUsuarioPermissionDto);
        return mvTabelaCupSession(collectionUsuarioPermissionDto.getSessionUUID());
    }

    @PutMapping("/cup")
    public ModelAndView alterarCupSession(@RequestBody CollectionUsuarioPermissionDto collectionUsuarioPermissionDto) {
        listasDeCupDtoSession.alterarCupSessionDto(collectionUsuarioPermissionDto);
        return mvTabelaCupSession(collectionUsuarioPermissionDto.getSessionUUID());
    }

    @DeleteMapping("/cup")
    public ModelAndView excluirCupSession(@RequestBody CollectionUsuarioPermissionDto collectionUsuarioPermissionDto) {
        listasDeCupDtoSession.excluirCupSessionDto(collectionUsuarioPermissionDto);
        return mvTabelaCupSession(collectionUsuarioPermissionDto.getSessionUUID());
    }

    // ---------------- TABELA CGP SESSION ----------------\\
    @GetMapping(value = "/cgp")
    public @ResponseBody List<CollectionGrupoPermissionDto> pesquisarGrupoParaTabelaCupSessionDto(String grupoNome,
                                                                                                  @AuthenticationPrincipal AppUser appUser) {
        return grupoRepository.findByNomeStartingWithIgnoreCaseAndDonoId(grupoNome, appUser.getUsuario().getId());
    }

    private ModelAndView mvTabelaCgpSession(String uuid) {
        ModelAndView modelAndView = new ModelAndView("collection/tabelaCgpSession");
        modelAndView.addObject("collectionGrupoPermissionDtoList", listasDeCgpDtoSession.getListCgpDtoSessionPorUUID(uuid));
        return modelAndView;
    }

    @PostMapping("/cgp")
    public ModelAndView adicionarCgpSession(@RequestBody CollectionGrupoPermissionDto collectionGrupoPermissionDto) {
        collectionGrupoPermissionDto.setPermission(AppPermission.PODE_VER_EXCETO_SENHAS);
        listasDeCgpDtoSession.addCgp(collectionGrupoPermissionDto.getSessionUUID(), collectionGrupoPermissionDto);
        return mvTabelaCgpSession(collectionGrupoPermissionDto.getSessionUUID());
    }

    @PutMapping("/cgp")
    public ModelAndView editarCgpSession(@RequestBody CollectionGrupoPermissionDto collectionGrupoPermissionDto) {
        listasDeCgpDtoSession.alterarCgp(collectionGrupoPermissionDto);
        return mvTabelaCgpSession(collectionGrupoPermissionDto.getSessionUUID());
    }

    @DeleteMapping("/cgp")
    public ModelAndView excluirCgpSession(@RequestBody CollectionGrupoPermissionDto collectionGrupoPermissionDto) {
        listasDeCgpDtoSession.excluirCgp(collectionGrupoPermissionDto);
        return mvTabelaCgpSession(collectionGrupoPermissionDto.getSessionUUID());
    }

    // ---------------- TABELA ITENS SESSION ----------------\\
    @GetMapping(value = "/item")
    public @ResponseBody List<ItemCollectionPesquisaDto> pesquisarItemParaTabelaItensSessionDto(String nome, @AuthenticationPrincipal AppUser appUser) {
        return itemRepository.findByNomeStartingWithIgnoreCaseAndDonoId(nome, appUser.getUsuario().getId());
    }

    private ModelAndView mvTabelaItensSession(String uuid) {
        ModelAndView modelAndView = new ModelAndView("collection/tabelaCiSession");
        modelAndView.addObject("itensDtoList", listasDeCollectionItensDtoSession.getListItensDtoSessionPorUUID(uuid));
        return modelAndView;
    }

    @PostMapping("/item")
    public ModelAndView adicionarItemSession(@RequestBody ItemCollectionPesquisaDto itemCollectionPesquisaDto) {
        listasDeCollectionItensDtoSession.addItem(itemCollectionPesquisaDto.getSessionUUID(), itemCollectionPesquisaDto);
        return mvTabelaItensSession(itemCollectionPesquisaDto.getSessionUUID());
    }

    @DeleteMapping("/item")
    public ModelAndView excluirItemSession(@RequestBody ItemCollectionPesquisaDto itemCollectionPesquisaDto) {
        listasDeCollectionItensDtoSession.excluirItem(itemCollectionPesquisaDto);
        return mvTabelaItensSession(itemCollectionPesquisaDto.getSessionUUID());
    }

    // ---------------- UTIL ----------------\\
    private void addListasFormDtoParaSession(CollectionFormDto collectionFormDto) {
        collectionFormDto.getCollectionsUsuariosPermissionsDtoList().forEach(
                cupDto -> listasDeCupDtoSession.adicionarCupSessionDto(collectionFormDto.getSessionUUID(), cupDto));

        collectionFormDto.getCollectionsGruposPermissionsDtoList().forEach(
                cgpDto -> listasDeCgpDtoSession.addCgp(collectionFormDto.getSessionUUID(), cgpDto));

        collectionFormDto.getItensDtoList().forEach(itemDto -> listasDeCollectionItensDtoSession.addItem(collectionFormDto.getSessionUUID(), itemDto));
    }

    private void addListasSessionParaFormDto(CollectionFormDto collectionFormDto) {
        collectionFormDto.addCupDtoList(listasDeCupDtoSession.getListCupSessionDtoPorUUID(collectionFormDto.getSessionUUID()));
        collectionFormDto.addCgpDtoList(listasDeCgpDtoSession.getListCgpDtoSessionPorUUID(collectionFormDto.getSessionUUID()));
        collectionFormDto.addItensDtoList(listasDeCollectionItensDtoSession.getListItensDtoSessionPorUUID(collectionFormDto.getSessionUUID()));
    }

    private void setUuidCollectionFormDto(CollectionFormDto collectionFormDto) {
        if (collectionFormDto.getSessionUUID() == null || collectionFormDto.getSessionUUID().isBlank()) {
            collectionFormDto.setSessionUUID(java.util.UUID.randomUUID().toString());
        }
    }

}
