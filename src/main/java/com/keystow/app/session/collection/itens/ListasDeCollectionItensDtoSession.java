package com.keystow.app.session.collection.itens;

import com.keystow.app.controller.list.ItemCollectionPesquisaDto;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SessionScope
@Component
public class ListasDeCollectionItensDtoSession {

    private final Set<CollectionItensDtoSession> itensDtoSessionSet = new HashSet<>();

    // @formatter:off
	private CollectionItensDtoSession getListasDeItensDtoSessionPorUUID(String uuid) {
		return itensDtoSessionSet.stream()
								   .filter(cgpList -> cgpList.getUuid().equals(uuid))
								   .findAny()
								   .orElse(new CollectionItensDtoSession(uuid));
	}

    public List<ItemCollectionPesquisaDto> getListItensDtoSessionPorUUID(String uuid) {
        return getListasDeItensDtoSessionPorUUID(uuid).getListItensDtoSession();
    }

    public void addItem(String uuid, ItemCollectionPesquisaDto itemCollectionPesquisaDto) {
        CollectionItensDtoSession itensDtoSessionList = getListasDeItensDtoSessionPorUUID(uuid);
        itensDtoSessionList.adicionar(itemCollectionPesquisaDto);
        itensDtoSessionSet.add(itensDtoSessionList);
    }

    public void excluirItem(ItemCollectionPesquisaDto itemCollectionPesquisaDto) {
        CollectionItensDtoSession itensDtoSessionList = getListasDeItensDtoSessionPorUUID(itemCollectionPesquisaDto.getSessionUUID());
        itensDtoSessionList.deletar(itemCollectionPesquisaDto);
    }

}
