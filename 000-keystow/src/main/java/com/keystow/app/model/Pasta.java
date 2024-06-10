package com.keystow.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Entity
@DynamicUpdate
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pasta {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@EqualsAndHashCode.Include
	private String id;

	@ManyToOne
	@JoinColumn(name = "dono_id")
	@NotNull
	private Dono dono;

	@NotNull(message = "O nome é obrigatório")
	@NotBlank(message = "O nome é obrigatório")
	private String nome;

	@OneToMany(mappedBy = "pasta") // EXPLICITAMENTE
	private List<ItemDoCofre> itensDoCofre;

	public boolean isNew() {
		return this.id == null || this.id.isBlank() || this.id.isEmpty();
	}

	@Override
	public String toString() {
		return "PASTA: " + this.id + ">>NOME: " + this.nome;
	}

}
