package com.keystow.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Uri {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@EqualsAndHashCode.Include
	private String id;

	private String valor;

	@ManyToOne
	@JoinColumn(name = "credencial_id")
	private Credencial credencial;

	@Override
	public String toString() {
		return "URI: " + this.id + ">>VALOR: " + this.valor;
	}

}
