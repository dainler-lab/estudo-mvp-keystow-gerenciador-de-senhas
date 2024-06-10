package com.keystow.app.mapper;

import com.keystow.app.controller.form.CredencialFormDto;
import com.keystow.app.model.Credencial;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Component
public class CredencialMapper {

	private final ModelMapper modelMapper;

	private final CampoPersonalizadoItemMapper campoPersonalizadoItemMapper;

	public CredencialFormDto toFormDto(Credencial credencial) {
		CredencialFormDto credencialFormDto = modelMapper.map(credencial, CredencialFormDto.class);
		credencialFormDto.setCamposPersonalizadosItensFormDtos(
				campoPersonalizadoItemMapper.toFormDtoList(credencial.getCamposPersonalizados()));
		return credencialFormDto;
	}

	public Credencial toModel(CredencialFormDto credencialFormDto) {
		Credencial credencial = modelMapper.map(credencialFormDto, Credencial.class);
		credencial.setCamposPersonalizadosItens(
				campoPersonalizadoItemMapper.toModelList(credencialFormDto.getCamposPersonalizadosFormDto()));
		return credencial;
	}

}
