package com.keystow.app.repository.filter;

import com.keystow.app.model.Dono;
import com.keystow.app.model.Pasta;
import lombok.Data;

@Data
public class PastaFilterListDto {

	private String id;

	private String nome;

	private Pasta pasta;

	private Dono dono;

}
