package com.keystow.app.controller.form;

import com.keystow.app.controller.list.CollectionGrupoPermissionDto;
import com.keystow.app.controller.list.CollectionUsuarioPermissionDto;
import com.keystow.app.controller.list.ItemCollectionPesquisaDto;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CollectionFormDto {

    private String sessionUUID;

    private String id;

    @Size(min = 3, max = 300, message = "O campo nome deve ter entre 3 e 300 caracteres")
    private String nome;

    private List<CollectionUsuarioPermissionDto> collectionsUsuariosPermissionsDtoList = new ArrayList<>();

    private List<CollectionGrupoPermissionDto> collectionsGruposPermissionsDtoList = new ArrayList<>();

    private List<ItemCollectionPesquisaDto> itensDtoList = new ArrayList<>();

    public void addCupDtoList(List<CollectionUsuarioPermissionDto> collectionsUsuariosPermissionsDtoList) {
        this.collectionsUsuariosPermissionsDtoList = collectionsUsuariosPermissionsDtoList;
        this.collectionsUsuariosPermissionsDtoList.forEach(cup -> cup.setCollectionId(this));
        collectionsUsuariosPermissionsDtoList.forEach(cup -> cup.setCollectionId(this));
    }

    public void addCgpDtoList(List<CollectionGrupoPermissionDto> collectionsGruposPermissionsDtoList) {
        this.collectionsGruposPermissionsDtoList = collectionsGruposPermissionsDtoList;
        this.collectionsGruposPermissionsDtoList.forEach(cgp -> cgp.setCollectionId(this));
        collectionsGruposPermissionsDtoList.forEach(cgp -> cgp.setCollectionId(this));
    }

    public void addItensDtoList(List<ItemCollectionPesquisaDto> itemsCollectionPesquisaDtoList) {
        this.itensDtoList = itemsCollectionPesquisaDtoList;
        this.itensDtoList.forEach(item -> item.getCollectionFormDtoList().add(this));
        itemsCollectionPesquisaDtoList.forEach(item -> item.getCollectionFormDtoList().add(this));
    }

    public boolean isNew() {
        return this.id == null || this.id.isBlank() || this.id.isEmpty();
    }

    @Override
    public String toString() {
        return "COLEÇÃO FORM DTO: " + this.id + ">>NOME: " + this.nome;
    }

}
