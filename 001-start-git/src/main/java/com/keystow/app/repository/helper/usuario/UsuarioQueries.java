package com.keystow.app.repository.helper.usuario;

import com.keystow.app.model.Usuario;
import com.keystow.app.repository.filter.UsuarioFilterListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioQueries {

	Page<Usuario> filtro(UsuarioFilterListDto usuarioFilterListDto, Pageable pageable);

}
