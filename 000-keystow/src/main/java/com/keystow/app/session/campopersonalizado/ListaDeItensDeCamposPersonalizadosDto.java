package com.keystow.app.session.campopersonalizado;

import com.keystow.app.controller.form.CampoPersonalizadoFormDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class ListaDeItensDeCamposPersonalizadosDto {

	@EqualsAndHashCode.Include
	private final String uuid;

	private final List<CampoPersonalizadoFormDto> itens = new ArrayList<>();

	public ListaDeItensDeCamposPersonalizadosDto(String uuid) {
		this.uuid = uuid;
	}

	private Optional<CampoPersonalizadoFormDto> getItemCampoPersonalizadoDtoById(String id) {
		return itens.stream().filter(i -> i.getId().equals(id)).findAny();
	}

	public void addItemCampoPersonalizado(CampoPersonalizadoFormDto itemAdd) {

		Optional<CampoPersonalizadoFormDto> itemOptional = getItemCampoPersonalizadoDtoById(itemAdd.getId());

		CampoPersonalizadoFormDto item = null;

		if (itemOptional.isPresent()) {
			item = itemOptional.get();
		}
		else {
			String id = UUID.randomUUID().toString();
			String nome = itemAdd.getNome() == null ? "" : itemAdd.getNome();
			String valor = itemAdd.getValor() == null ? "" : itemAdd.getValor();
			item = new CampoPersonalizadoFormDto();
			item.setId(id);
			item.setTipoDeCampoPersonalizado(itemAdd.getTipoDeCampoPersonalizado());
			item.setNome(nome);
			item.setValor(valor);
			itens.add(item);
		}
	}

	public void alterarItemCampoPersonalizado(CampoPersonalizadoFormDto itemAlterar) {

		Optional<CampoPersonalizadoFormDto> itemOptional = getItemCampoPersonalizadoDtoById(itemAlterar.getId());

		if (itemOptional.isPresent()) {
			CampoPersonalizadoFormDto item = itemOptional.get();
			item.setNome(itemAlterar.getNome());
			item.setValor(itemAlterar.getValor());
		}
	}

	public void excluirItemCampoPersonalizado(String id) {
		Optional<CampoPersonalizadoFormDto> itemOptional = getItemCampoPersonalizadoDtoById(id);
		itemOptional.ifPresent(itens::remove);
	}

	/**
	 * public void excluirItemCampoPersonalizado(CampoPersonalizadoFormDto itemDto) { int
	 * indice = IntStream.range(0, itens.size()).filter(i ->
	 * itens.get(i).equals(itemDto)).findAny().getAsInt(); itens.remove(indice); }
	 */

	@Override
	public String toString() {
		return "LISTA DE CP: " + this.itens;
	}

}
