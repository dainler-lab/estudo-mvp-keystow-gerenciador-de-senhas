package com.keystow.app.mapper;

import com.keystow.app.controller.form.GrupoFormDto;
import com.keystow.app.model.Grupo;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Component
public class GrupoMapper {

    private final ModelMapper modelMapper;

    public GrupoFormDto toFormDto(Grupo grupo) {
        return modelMapper.map(grupo, GrupoFormDto.class);
    }

    public Grupo toModel(GrupoFormDto grupoFormDto) {
        return modelMapper.map(grupoFormDto, Grupo.class);
    }

}
