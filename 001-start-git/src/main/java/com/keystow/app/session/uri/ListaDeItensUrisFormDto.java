package com.keystow.app.session.uri;

import com.keystow.app.controller.form.UriFormDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class ListaDeItensUrisFormDto {

	private final String uuid;

	private final List<UriFormDto> itens = new ArrayList<>();

	ListaDeItensUrisFormDto(String uuid) {
		this.uuid = uuid;
	}

	private Optional<UriFormDto> getItemUriPorId(String id) {
		return itens.stream().filter(i -> i.getId().equals(id)).findAny();
	}

	public void addItemUri(UriFormDto itemUriAdd) {

		Optional<UriFormDto> itemOptional = getItemUriPorId(itemUriAdd.getId());

		UriFormDto uriFormDto;

		if (itemOptional.isPresent()) {
			uriFormDto = itemOptional.get();
		}
		else {
			String id = UUID.randomUUID().toString();
			String valor = itemUriAdd.getValor() == null ? "" : itemUriAdd.getValor();
			uriFormDto = new UriFormDto();
			uriFormDto.setId(id);
			uriFormDto.setValor(valor);
			uriFormDto.setCredencialFormDto(itemUriAdd.getCredencialFormDto());
			uriFormDto.setItemFormUUID(itemUriAdd.getItemFormUUID());
			itens.add(uriFormDto);
		}
	}

	public void alterarItemUri(UriFormDto itemUriAlterar) {
		Optional<UriFormDto> itemOptional = getItemUriPorId(itemUriAlterar.getId());
		if (itemOptional.isPresent()) {
			UriFormDto uriFormDto = itemOptional.get();
			uriFormDto.setValor(itemUriAlterar.getValor());
		}
	}

	public void excluirItemUri(String id) {
		Optional<UriFormDto> itemOptional = getItemUriPorId(id);
		itemOptional.ifPresent(itens::remove);
	}

	public String getUuid() {
		return uuid;
	}

	public List<UriFormDto> getItens() {
		return itens;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ListaDeItensUrisFormDto that = (ListaDeItensUrisFormDto) o;
		return uuid.equals(that.uuid);
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

}
