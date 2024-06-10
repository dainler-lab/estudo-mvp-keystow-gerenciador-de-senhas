package com.keystow.app.controller.form;

import com.keystow.app.model.Dono;
import com.keystow.app.model.Pasta;
import com.keystow.app.model.enuns.TipoDeItemDoCofre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ItemFormDto {

	private String id;

	@NotNull(message = "O tipo de item é obrigatório")
	private TipoDeItemDoCofre tipoDeItemDoCofre;

	private Dono dono;

	private Pasta pasta;

	@NotBlank(message = "O campo nome não pode ser vazio")
	@Size(min = 2, max = 200, message = "O tamanho do nome deve ser entre 2 e 200 caracteres")
	private String nome;

	private List<CampoPersonalizadoFormDto> camposPersonalizadosFormDto = new ArrayList<>();

	private Boolean favorito;

	private Boolean lixeira;

	private String nota;

	private LocalDateTime criadoEm;

	private LocalDateTime atualizadoEm;

	private String itemFormUUID;

	public void setCamposPersonalizadosItensFormDtos(List<CampoPersonalizadoFormDto> camposPersonalizadosFormDtos) {
		this.camposPersonalizadosFormDto = camposPersonalizadosFormDtos;
		this.camposPersonalizadosFormDto.forEach(campoPersonalizadoItem -> campoPersonalizadoItem.setItemFormDto(this));
	}

	public boolean isNew() {
		return this.id == null || this.id.isBlank() || this.id.isEmpty();
	}

	@Override
	public String toString() {
		return "ItemCollectionPesquisaDto: " + getId();
	}

}
