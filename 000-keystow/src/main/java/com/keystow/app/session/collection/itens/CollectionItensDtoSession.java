package com.keystow.app.session.collection.itens;

import com.keystow.app.controller.list.ItemCollectionPesquisaDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
class CollectionItensDtoSession {

    private final String uuid;

    private final List<ItemCollectionPesquisaDto> listItensDtoSession = new ArrayList<>();

    public CollectionItensDtoSession(String uuid) {
        this.uuid = uuid;
    }

    private Optional<ItemCollectionPesquisaDto> getItemPesquisaDtoById(String itemId) {
        return listItensDtoSession.stream().filter(item -> item.getId().equals(itemId)).findAny();
    }

    public void adicionar(ItemCollectionPesquisaDto itemCollectionPesquisaDto) {
        if (getItemPesquisaDtoById(itemCollectionPesquisaDto.getId()).isPresent() || itemCollectionPesquisaDto.getId() == null) return;
        listItensDtoSession.add(itemCollectionPesquisaDto);
    }

    public void deletar(ItemCollectionPesquisaDto itemCollectionPesquisaDto) {
        listItensDtoSession.removeIf(item -> item.getId().equals(itemCollectionPesquisaDto.getId()));
    }

}
