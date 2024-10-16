package com.keystow.app.session.collection.cup;

import com.keystow.app.controller.list.CollectionUsuarioPermissionDto;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SessionScope
@Component
public class ListasDeCupDtoSession {

	private final Set<TabelaCollectionUsuarioPermissionSessionDto> tabelasCupSessionDto = new HashSet<>();

	// @formatter:off
	private TabelaCollectionUsuarioPermissionSessionDto getTabelaCupSessionDtoPorUUID(String uuid) {
		return tabelasCupSessionDto.stream()
								   .filter(tabela -> tabela.getUuid().equals(uuid))
								   .findAny()
								   .orElse(new TabelaCollectionUsuarioPermissionSessionDto(uuid));
	}

    public List<CollectionUsuarioPermissionDto> getListCupSessionDtoPorUUID(String uuid) {
        return getTabelaCupSessionDtoPorUUID(uuid).getCupSessionDtoList();
    }

    public void adicionarCupSessionDto(String uuid, CollectionUsuarioPermissionDto collectionUsuarioPermissionDto) {
        TabelaCollectionUsuarioPermissionSessionDto tabelaCupSessionDto = getTabelaCupSessionDtoPorUUID(uuid);
        tabelaCupSessionDto.createCupSessionDto(collectionUsuarioPermissionDto);
        tabelasCupSessionDto.add(tabelaCupSessionDto);
    }

    public void alterarCupSessionDto(CollectionUsuarioPermissionDto collectionUsuarioPermissionDto) {
        TabelaCollectionUsuarioPermissionSessionDto tabelaCupSessionDto = getTabelaCupSessionDtoPorUUID(collectionUsuarioPermissionDto.getSessionUUID());
        tabelaCupSessionDto.updateCupSessionDto(collectionUsuarioPermissionDto);
    }

    public void excluirCupSessionDto(CollectionUsuarioPermissionDto collectionUsuarioPermissionDto) {
        TabelaCollectionUsuarioPermissionSessionDto tabelaCupSessionDto = getTabelaCupSessionDtoPorUUID(collectionUsuarioPermissionDto.getSessionUUID());
        tabelaCupSessionDto.deleteCupSessionDto(collectionUsuarioPermissionDto);
    }

}
