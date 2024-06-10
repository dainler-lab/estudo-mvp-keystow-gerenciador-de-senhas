package com.keystow.app.repository.helper.collection;

import com.keystow.app.model.collection.CollectionDeItensDoCofre;
import com.keystow.app.repository.filter.CollectionFilterListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CollectionQueries {

	Page<CollectionDeItensDoCofre> filtro(CollectionFilterListDto collectionFilterListDto, Pageable pageable);

}
