package com.keystow.app.model.collection;

import com.keystow.app.model.Grupo;
import com.keystow.app.model.enuns.AppPermission;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "collections_grupos_permissions")
@DynamicUpdate
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class CollectionGrupoPermission {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@EqualsAndHashCode.Include
	private String id;

	@ManyToOne
	@JoinColumn(name = "collection_de_itens_do_cofre_id")
	@NotNull
	private CollectionDeItensDoCofre collectionDeItensDoCofre;

	@ManyToOne
	@JoinColumn(name = "grupo_id")
	@NotNull
	private Grupo grupo;

	@Enumerated(EnumType.STRING)
	@NotNull
	private AppPermission permission;

	public boolean isNew() {
		return this.id == null || this.id.isBlank() || this.id.isEmpty();
	}

	@Override
	public String toString() {
		return "CGP: " + this.permission.name();
	}

}
