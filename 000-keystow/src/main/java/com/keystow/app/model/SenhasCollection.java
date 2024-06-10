package com.keystow.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@DynamicUpdate
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SenhasCollection {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@EqualsAndHashCode.Include
	private String id;

	@ManyToOne
	@JoinColumn(name = "dono_id")
	@NotNull
	private Dono dono;

	@NotNull
	@NotBlank
	private String valor;

	@Column(updatable = false)
	@NotNull
	private LocalDateTime criadoEm;

	@Transient
	private String criadoEmFormattedDate;

	@Override
	public String toString() {
		return "COLEÇÃO DE SENHAS: " + this.id;
	}

}
