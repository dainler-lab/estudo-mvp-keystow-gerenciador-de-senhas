package com.keystow.app.repository.helper.grupo;

import com.keystow.app.model.Grupo;
import com.keystow.app.repository.filter.GrupoFilterListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GrupoQueries {

	Page<Grupo> filtro(GrupoFilterListDto grupoFilterListDto, Pageable pageable);

}
