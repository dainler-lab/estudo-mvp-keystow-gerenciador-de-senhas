package com.keystow.app.repository.helper.credencial;

import com.keystow.app.model.Credencial;
import com.keystow.app.repository.filter.CredencialFilterListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CredencialQueries {

	Page<Credencial> filtro(CredencialFilterListDto credencialFilterListDto, Pageable pageable);

	Page<Credencial> filtroDonoAndLixeira(CredencialFilterListDto credencialFilterListDto, Pageable pageable);

	Credencial getCredencialComCamposPersonalizados(String id);

}
