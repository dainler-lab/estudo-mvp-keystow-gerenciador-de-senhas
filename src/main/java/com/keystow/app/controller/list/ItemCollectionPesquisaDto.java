package com.keystow.app.controller.list;

import com.keystow.app.controller.form.CollectionFormDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemCollectionPesquisaDto {

	private String sessionUUID;

	@NotNull
	private String id;

	@NotNull
	@NotBlank
	private String nome;

	private List<CollectionFormDto> collectionFormDtoList = new ArrayList<>();

	public ItemCollectionPesquisaDto(String id, String nome) {
		this.id = id;
		this.nome = nome;
	}

	@Override
	public String toString() {
		return "ITEM LIST DTO: " + this.nome;
	}

}
