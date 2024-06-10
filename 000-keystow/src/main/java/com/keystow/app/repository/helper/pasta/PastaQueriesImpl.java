package com.keystow.app.repository.helper.pasta;

import com.keystow.app.model.Pasta;
import com.keystow.app.page.PaginationUtil;
import com.keystow.app.repository.filter.PastaFilterListDto;
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
public class PastaQueriesImpl implements PastaQueries {

	private final PaginationUtil paginationUtil;

	private EntityManager entityManager;

	@Override
	@Transactional(readOnly = true)
	public Page<Pasta> filtro(PastaFilterListDto pastaFilterListDto, Pageable pageable) {
		CriteriaBuilder criteriaBuilderPasta = entityManager.getCriteriaBuilder();
		CriteriaQuery<Pasta> criteriaQueryPasta = criteriaBuilderPasta.createQuery(Pasta.class);
		Root<Pasta> rootPasta = criteriaQueryPasta.from(Pasta.class);
		criteriaQueryPasta.select(rootPasta); // EXPLICITAMENTE

		if (pastaFilterListDto.getDono() == null) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}

		Predicate[] predicatesPasta = adicionarFiltro(pastaFilterListDto, criteriaBuilderPasta, rootPasta);
		criteriaQueryPasta.where(predicatesPasta);

		TypedQuery<Pasta> typedQueryPasta = paginationUtil.prepare(entityManager, criteriaBuilderPasta,
				criteriaQueryPasta, rootPasta, pageable);

		return new PageImpl<>(typedQueryPasta.getResultList(), pageable, total(pastaFilterListDto));
	}

	private Predicate[] adicionarFiltro(PastaFilterListDto pastaFilterListDto, CriteriaBuilder criteriaBuilderPasta,
			Root<Pasta> rootPasta) {
		List<Predicate> predicatesPasta = new ArrayList<>();
		if (pastaFilterListDto.getDono() != null) {
			if (pastaFilterListDto.getPasta() != null && StringUtils.hasText(pastaFilterListDto.getPasta().getId())) {
				predicatesPasta
					.add(criteriaBuilderPasta.equal(rootPasta.get("id"), pastaFilterListDto.getPasta().getId()));
			}

			if (StringUtils.hasText(pastaFilterListDto.getNome())) {
				predicatesPasta.add(criteriaBuilderPasta.like(criteriaBuilderPasta.lower(rootPasta.get("nome")),
						"%" + pastaFilterListDto.getNome().toLowerCase() + "%"));
			}

			if (StringUtils.hasText(pastaFilterListDto.getDono().getId())) {
				predicatesPasta.add(criteriaBuilderPasta.equal(rootPasta.get("dono").get("id"),
						pastaFilterListDto.getDono().getId()));
			}
		}
		return predicatesPasta.toArray(new Predicate[0]);
	}

	private long total(PastaFilterListDto pastaFilterListDto) {
		CriteriaBuilder criteriaBuilderPasta = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQueryPasta = criteriaBuilderPasta.createQuery(Long.class);
		Root<Pasta> rootPasta = criteriaQueryPasta.from(Pasta.class);

		criteriaQueryPasta.select(criteriaBuilderPasta.count(rootPasta));
		Predicate[] predicatesPasta = adicionarFiltro(pastaFilterListDto, criteriaBuilderPasta, rootPasta);
		criteriaQueryPasta.where(predicatesPasta);

		return entityManager.createQuery(criteriaQueryPasta).getSingleResult();
	}

}
