package com.keystow.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Entity
@DiscriminatorValue("Usuario")
@DynamicUpdate
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Usuario extends Dono {

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

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    private String senha;

    @NotNull
    private Boolean ativo = true;

    @ManyToMany
    @JoinTable(name = "usuarios_grupos", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "grupo_id"))
    private List<Grupo> grupos;

    public boolean isNew() {
        return this.id == null || this.id.isBlank() || this.id.isEmpty();
    }

    @Override
    public String toString() {
        return "USUARIO: " + this.id + ">>NOME: " + this.nome;
    }

}
