package com.keystow.app.model;

import com.keystow.app.model.collection.CollectionDeItensDoCofre;
import com.keystow.app.model.enuns.TipoDeItemDoCofre;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "classe")
@DynamicUpdate
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemDoCofre {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private String id;

    @ManyToOne
    @JoinColumn(name = "dono_id")
    @NotNull
    private Dono dono;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoDeItemDoCofre tipoDeItemDoCofre;

    @NotNull
    @NotBlank
    private String nome;

    @OneToMany(mappedBy = "itemDoCofre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampoPersonalizado> camposPersonalizados = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "pasta_id")
    private Pasta pasta;

    @ManyToMany(mappedBy = "itensList")
    private List<CollectionDeItensDoCofre> collectionsList = new ArrayList<>();

    @ManyToMany(mappedBy = "itensDoCofre")
    private List<Grupo> gruposList = new ArrayList<>();

    @NotNull
    @Column(columnDefinition = "boolean default false")
    private Boolean favorito = false;

    @NotNull
    @Column(columnDefinition = "boolean default false")
    private Boolean lixeira = false;

    @Column(updatable = false)
    @NotNull
    private LocalDateTime criadoEm;

    @NotNull
    private LocalDateTime atualizadoEm;

    private String nota;

    public void setCamposPersonalizadosItens(List<CampoPersonalizado> camposPersonalizados) {
        this.camposPersonalizados = camposPersonalizados;
        this.camposPersonalizados.forEach(campoPersonalizado -> campoPersonalizado.setItemDoCofre(this));
        camposPersonalizados.forEach(campoPersonalizado -> campoPersonalizado.setItemDoCofre(this));
    }

    public void addCollectionsModelList(List<CollectionDeItensDoCofre> collectionsList) {
        this.collectionsList = collectionsList;
        this.collectionsList.forEach(collection -> collection.getItensList().add(this));
        collectionsList.forEach(collection -> collection.getItensList().add(this));
    }

    public boolean isNew() {
        return this.id == null || this.id.isBlank() || this.id.isEmpty();
    }

    @Override
    public String toString() {
        return "ITEM DO COFRE: " + this.id + ">>NOME: " + this.nome;
    }

}
