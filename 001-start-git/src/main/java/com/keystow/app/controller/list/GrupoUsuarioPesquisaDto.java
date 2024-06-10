package com.keystow.app.controller.list;

import com.keystow.app.controller.form.GrupoFormDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GrupoUsuarioPesquisaDto {

    private String sessionUUID;

    @NotNull
    private String id;

    @NotNull
    @NotBlank
    private String nome;

    @NotNull
    @NotBlank
    private String email;

    private List<GrupoFormDto> grupos = new ArrayList<>();

    public GrupoUsuarioPesquisaDto(String id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    @Override
    public String toString() {
        return "GRUPO USERS LIST DTO: " + this.nome;
    }

}
