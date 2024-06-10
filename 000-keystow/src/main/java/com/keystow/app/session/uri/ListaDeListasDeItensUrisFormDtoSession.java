package com.keystow.app.session.uri;

import com.keystow.app.controller.form.UriFormDto;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SessionScope
@Component
public class ListaDeListasDeItensUrisFormDtoSession {

	private final Set<ListaDeItensUrisFormDto> listaDeListasDeItensUris = new HashSet<>();

	public void addItemUriListaDeListas(String uuid, UriFormDto itemUriAdd) {
		ListaDeItensUrisFormDto listaDeItensUrisFormDto = getListaDeItensUrisFormDtosPorUUID(uuid);
		listaDeItensUrisFormDto.addItemUri(itemUriAdd);
		listaDeListasDeItensUris.add(listaDeItensUrisFormDto);
	}

	public void alterarItemUriListaDeListas(String uuid, UriFormDto itemUriAlterar) {
		ListaDeItensUrisFormDto listaDeItensUrisFormDto = getListaDeItensUrisFormDtosPorUUID(uuid);
		listaDeItensUrisFormDto.alterarItemUri(itemUriAlterar);
	}

	public void excluirItemUriListaDeListas(String uuid, String id) {
		ListaDeItensUrisFormDto listaDeItensUrisFormDto = getListaDeItensUrisFormDtosPorUUID(uuid);
		listaDeItensUrisFormDto.excluirItemUri(id);
	}

	public List<UriFormDto> getListUriFormDtosPorUUID(String uuid) {
		return getListaDeItensUrisFormDtosPorUUID(uuid).getItens();
	}

	// @formatter:off
    private ListaDeItensUrisFormDto getListaDeItensUrisFormDtosPorUUID(String uuid) {
        return listaDeListasDeItensUris.stream()
                                       .filter(l -> l.getUuid().equals(uuid))
                                       .findAny()
                                       .orElse(new ListaDeItensUrisFormDto(uuid));
    }
}
