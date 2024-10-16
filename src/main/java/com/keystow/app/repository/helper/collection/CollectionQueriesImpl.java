package com.keystow.app.repository.helper.collection;

import com.keystow.app.model.collection.CollectionDeItensDoCofre;
import com.keystow.app.page.PaginationUtil;
import com.keystow.app.repository.filter.CollectionFilterListDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class CollectionQueriesImpl implements CollectionQueries {

	private final PaginationUtil paginationUtil;

	private final EntityManager entityManager;

	@Override
	@Transactional(readOnly = true)
	public Page<CollectionDeItensDoCofre> filtro(CollectionFilterListDto collectionFilterListDto, Pageable pageable) {
		CriteriaBuilder criteriaBuilderCollection = entityManager.getCriteriaBuilder();
		CriteriaQuery<CollectionDeItensDoCofre> criteriaQueryCollection = criteriaBuilderCollection
			.createQuery(CollectionDeItensDoCofre.class);
		Root<CollectionDeItensDoCofre> rootCollection = criteriaQueryCollection.from(CollectionDeItensDoCofre.class);
		criteriaQueryCollection.select(rootCollection); // EXPLICITAMENTE

		if (collectionFilterListDto.getDono() == null) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}

		Predicate[] predicatesCollection = adicionarFiltro(collectionFilterListDto, criteriaBuilderCollection,
				rootCollection);
		criteriaQueryCollection.where(predicatesCollection);

		TypedQuery<CollectionDeItensDoCofre> typedQueryCollection = paginationUtil.prepare(entityManager,
				criteriaBuilderCollection, criteriaQueryCollection, rootCollection, pageable);

		return new PageImpl<>(typedQueryCollection.getResultList(), pageable, total(collectionFilterListDto));
	}

	private Predicate[] adicionarFiltro(CollectionFilterListDto collectionFilterListDto,
			CriteriaBuilder criteriaBuilderCollection, Root<CollectionDeItensDoCofre> rootCollection) {

		List<Predicate> predicatesCollection = new ArrayList<>();

		if (collectionFilterListDto.getDono() != null) {

			if (StringUtils.hasText(collectionFilterListDto.getDono().getId())) {
				predicatesCollection.add(criteriaBuilderCollection.equal(rootCollection.get("dono").get("id"),
						collectionFilterListDto.getDono().getId()));
			}

			if (StringUtils.hasText(collectionFilterListDto.getNome())) {
				predicatesCollection
					.add(criteriaBuilderCollection.like(criteriaBuilderCollection.lower(rootCollection.get("nome")),
							"%" + collectionFilterListDto.getNome().toLowerCase() + "%"));
			}
		}
		return predicatesCollection.toArray(new Predicate[0]);
	}

	private long total(CollectionFilterListDto collectionFilterListDto) {
		CriteriaBuilder criteriaBuilderCollection = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQueryCollection = criteriaBuilderCollection.createQuery(Long.class);
		Root<CollectionDeItensDoCofre> rootCollection = criteriaQueryCollection.from(CollectionDeItensDoCofre.class);

		criteriaQueryCollection.select(criteriaBuilderCollection.count(rootCollection));
		Predicate[] predicatesCollection = adicionarFiltro(collectionFilterListDto, criteriaBuilderCollection,
				rootCollection);
		criteriaQueryCollection.where(predicatesCollection);

		return entityManager.createQuery(criteriaQueryCollection).getSingleResult();
	}

}
