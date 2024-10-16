package com.keystow.app.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppPermission {

    PODE_VER("Pode ver"), PODE_VER_EXCETO_SENHAS("Pode ver exceto senhas"),

    PODE_EDITAR("Pode editar"), PODE_EDITAR_EXCETO_SENHAS("Pode editar exceto senhas"),

    PODE_GERENCIAR("Pode gerenciar");

    private final String description;

}
