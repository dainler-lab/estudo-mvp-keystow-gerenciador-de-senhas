package com.keystow.app.controller.form;

import com.keystow.app.controller.list.GrupoUsuarioPesquisaDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GrupoFormDto {

    private String sessionUUID;

    private String id;

    @NotNull(message = "O nome é obrigatório")
    @NotBlank(message = "O nome não deve estar vazio ou em branco")
    @Size(min = 3, max = 300, message = "O campo nome deve ter entre 3 e 300 caracteres")
    private String nome;

    private List<GrupoUsuarioPesquisaDto> usuarios = new ArrayList<>();

    public boolean isNew() {
        return this.id == null || this.id.isBlank() || this.id.isEmpty();
    }

    @Override
    public String toString() {
        return "GRUPO FORM DTO: " + ">>NOME: " + this.nome;
    }

}
