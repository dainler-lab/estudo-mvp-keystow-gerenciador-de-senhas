package com.keystow.app.mapper;

import com.keystow.app.controller.form.CampoPersonalizadoFormDto;
import com.keystow.app.model.CampoPersonalizado;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Component
public class CampoPersonalizadoItemMapper {

	private final ModelMapper modelMapper;

	public CampoPersonalizadoFormDto toFormDto(CampoPersonalizado campoPersonalizado) {
		return modelMapper.map(campoPersonalizado, CampoPersonalizadoFormDto.class);
	}

	public CampoPersonalizado toModel(CampoPersonalizadoFormDto campoPersonalizadoFormDto) {
		return modelMapper.map(campoPersonalizadoFormDto, CampoPersonalizado.class);
	}

	public List<CampoPersonalizadoFormDto> toFormDtoList(List<CampoPersonalizado> camposPersonalizadosList) {
		return camposPersonalizadosList.stream().map(this::toFormDto).collect(Collectors.toList());
	}

	public List<CampoPersonalizado> toModelList(List<CampoPersonalizadoFormDto> camposPersonalizadosDtoList) {
		return camposPersonalizadosDtoList.stream().map(this::toModel).collect(Collectors.toList());
	}

}
