package com.keystow.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Entity
@DiscriminatorValue("Grupo")
@DynamicUpdate
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Grupo extends Dono {

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

    @ManyToMany(mappedBy = "grupos")
    private List<Usuario> usuarios;

    @ManyToMany
    @JoinTable(name = "grupos_itens_do_cofre", joinColumns = @JoinColumn(name = "grupo_id"), inverseJoinColumns = @JoinColumn(name = "item_do_cofre_id"))
    private List<ItemDoCofre> itensDoCofre;

    public boolean isNew() {
        return this.id == null || this.id.isBlank() || this.id.isEmpty();
    }

    @Override
    public String toString() {
        return "GRUPO: " + this.id + ">>NOME: " + this.nome;
    }

}
