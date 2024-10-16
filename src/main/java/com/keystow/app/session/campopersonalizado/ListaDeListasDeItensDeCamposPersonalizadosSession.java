package com.keystow.app.session.campopersonalizado;

import com.keystow.app.controller.form.CampoPersonalizadoFormDto;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SessionScope
@Component
public class ListaDeListasDeItensDeCamposPersonalizadosSession {

	private final Set<ListaDeItensDeCamposPersonalizadosDto> listaDeListasDeItens = new HashSet<>();

	public void addItemCampoPersonalizado(String uuid, CampoPersonalizadoFormDto campoPersonalizadoFormDto) {
		ListaDeItensDeCamposPersonalizadosDto listaDeItens = getListaDeItensPorUUID(uuid);
		listaDeItens.addItemCampoPersonalizado(campoPersonalizadoFormDto);
		listaDeListasDeItens.add(listaDeItens);
	}

	public void alterarItemCampoPersonalizado(String uuid, CampoPersonalizadoFormDto campoPersonalizadoFormDto) {
		ListaDeItensDeCamposPersonalizadosDto listaDeItens = getListaDeItensPorUUID(uuid);
		listaDeItens.alterarItemCampoPersonalizado(campoPersonalizadoFormDto);
	}

	public void excluirItemCampoPersonalizado(String uuid, String id) {
		ListaDeItensDeCamposPersonalizadosDto listaDeItens = getListaDeItensPorUUID(uuid);
		listaDeItens.excluirItemCampoPersonalizado(id);
	}

	public List<CampoPersonalizadoFormDto> getListaDeItensDeCamposPersonalizadosDto(String uuid) {
		return getListaDeItensPorUUID(uuid).getItens();
	}

	// @formatter:off
    private ListaDeItensDeCamposPersonalizadosDto getListaDeItensPorUUID(String uuid) {
        return listaDeListasDeItens.stream()
                                   .filter(l -> l.getUuid().equals(uuid))
                                   .findAny()
                                   .orElse(new ListaDeItensDeCamposPersonalizadosDto(uuid));
    }

	@Override
	public String toString() {
		return "LISTA DE LISTAS DE CP: " + this.listaDeListasDeItens;
	}
}
