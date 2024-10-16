package com.keystow.app.repository.filter;

import com.keystow.app.model.Dono;
import lombok.Data;

@Data
public class CollectionFilterListDto {

	private String id;

	private String nome;

	private Dono dono;

}
