package com.keystow.app.repository.helper.pasta;

import com.keystow.app.model.Pasta;
import com.keystow.app.repository.filter.PastaFilterListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PastaQueries {

	Page<Pasta> filtro(PastaFilterListDto credencialFilterListDto, Pageable pageable);

}
