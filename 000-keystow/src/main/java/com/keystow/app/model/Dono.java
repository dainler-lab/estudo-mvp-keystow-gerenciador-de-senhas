package com.keystow.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "classe")
@DynamicUpdate
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Dono {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@EqualsAndHashCode.Include
	private String id;

	// @OneToMany(mappedBy = "dono")CollectionUsuarioPermissionDto
	// private List<ItemDoCofre> itensDoCofre;
	//
	// @OneToMany(mappedBy = "dono")CollectionUsuarioPermissionDto
	// private List<Pasta> pastas;
	//
	// @OneToMany(mappedBy = "dono")CollectionUsuarioPermissionDto
	// private List<Usuario> usuarios;
	//
	// @OneToMany(mappedBy = "dono")CollectionUsuarioPermissionDto
	// private List<CollectionDeItensDoCofre> collectionsDeItensDoCofre;
	//
	// @OneToMany(mappedBy = "dono")CollectionUsuarioPermissionDto
	// private List<Grupo> grupos;
	//
	// @OneToMany(mappedBy = "dono")CollectionUsuarioPermissionDto
	// private List<SenhasCollection> senhasCollections;

	@Column(updatable = false)
	@NotNull
	private LocalDateTime criadoEm;

	@NotNull
	private LocalDateTime atualizadoEm;

	@Override
	public String toString() {
		return "DONO: " + id;
	}

}
