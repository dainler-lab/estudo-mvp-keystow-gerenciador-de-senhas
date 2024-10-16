package com.keystow.app.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("Credencial")
@DynamicUpdate
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Credencial extends ItemDoCofre {

	@NotNull
	@NotBlank
	private String nomeDeUsuario;

	@NotNull
	@NotBlank
	private String senha;

	@OneToMany(mappedBy = "credencial", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Uri> uris = new ArrayList<>();

	public void setUris(List<Uri> uris) {
		this.uris = uris;
		this.uris.forEach(uri -> uri.setCredencial(this));
	}

	@Override
	public String toString() {
		return "CREDENCIAL: " + getId() + ">>NOME: " + getNome();
	}

}
