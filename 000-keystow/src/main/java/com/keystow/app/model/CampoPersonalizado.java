package com.keystow.app.model;

import com.keystow.app.model.enuns.TipoDeCampoPersonalizado;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CampoPersonalizado {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@EqualsAndHashCode.Include
	private String id;

	@Enumerated(EnumType.STRING)
	@NotNull
	private TipoDeCampoPersonalizado tipoDeCampoPersonalizado;

	private String nome;

	private String valor;

	@ManyToOne
	@JoinColumn(name = "item_do_cofre_id")
	private ItemDoCofre itemDoCofre;

	@Override
	public String toString() {
		return "CAMPO PERSONALIZADO: " + this.id + ">>NOME: " + this.nome;
	}

}
