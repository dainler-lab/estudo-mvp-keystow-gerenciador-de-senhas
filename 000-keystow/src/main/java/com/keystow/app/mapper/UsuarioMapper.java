package com.keystow.app.mapper;

import com.keystow.app.controller.form.UsuarioFormDto;
import com.keystow.app.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Component
public class UsuarioMapper {

	private final ModelMapper modelMapper;

	public UsuarioFormDto toFormDto(Usuario usuario) {
		return modelMapper.map(usuario, UsuarioFormDto.class);
	}

	public Usuario toModel(UsuarioFormDto usuarioFormDto) {
		return modelMapper.map(usuarioFormDto, Usuario.class);
	}

}
