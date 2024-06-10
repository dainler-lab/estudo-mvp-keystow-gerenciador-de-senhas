package com.keystow.app.repository.filter;

import com.keystow.app.model.Dono;
import lombok.Data;

@Data
public class GrupoFilterListDto {

	private String id;

	private String nome;

	private Dono dono;

}
