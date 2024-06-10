package com.keystow.app.model.collection;

import com.keystow.app.model.Dono;
import com.keystow.app.model.ItemDoCofre;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@DynamicUpdate
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CollectionDeItensDoCofre {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private String id;

    @ManyToOne
    @JoinColumn(name = "dono_id")
    @NotNull
    private Dono dono;

    @NotNull
    private String nome;

    @OneToMany(mappedBy = "collectionDeItensDoCofre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CollectionUsuarioPermission> collectionsUsuariosPermissionsList = new ArrayList<>();

    @OneToMany(mappedBy = "collectionDeItensDoCofre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CollectionGrupoPermission> collectionsGruposPermissionsList = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "itens_collections", joinColumns = @JoinColumn(name = "collection_de_itens_do_cofre_id"),
               inverseJoinColumns = @JoinColumn(name = "item_do_cofre_id"))
    private List<ItemDoCofre> itensList = new ArrayList<>();

    @Column(updatable = false)
    @NotNull
    private LocalDateTime criadoEm;

    // @Column(insertable = false)
    @NotNull
    private LocalDateTime atualizadoEm;

    public void addCollectionsUsuariosPermissionsModelList(List<CollectionUsuarioPermission> collectionsUsuariosPermissions) {
        this.collectionsUsuariosPermissionsList = collectionsUsuariosPermissions;
        this.collectionsUsuariosPermissionsList.forEach(cup -> cup.setCollectionDeItensDoCofre(this));
        collectionsUsuariosPermissions.forEach(cup -> cup.setCollectionDeItensDoCofre(this));
    }

    public void addCollectionsGrupoPermissionsModelList(List<CollectionGrupoPermission> collectionsGruposPermissionsList) {
        this.collectionsGruposPermissionsList = collectionsGruposPermissionsList;
        this.collectionsGruposPermissionsList.forEach(cgp -> cgp.setCollectionDeItensDoCofre(this));
        collectionsGruposPermissionsList.forEach(cgp -> cgp.setCollectionDeItensDoCofre(this));
    }

    public void addItensModelList(List<ItemDoCofre> itensDoCofreList) {
        this.itensList = itensDoCofreList;
        this.itensList.forEach(item -> item.getCollectionsList().add(this));
        itensDoCofreList.forEach(item -> item.getCollectionsList().add(this));
    }

    public boolean isNew() {
        return this.id == null || this.id.isBlank() || this.id.isEmpty();
    }

    @Override
    public String toString() {
        return "COLEÇÃO: " + this.id + ">>NOME: " + this.nome;
    }

}
