package com.keystow.app.repository.helper.grupo;

import com.keystow.app.model.Grupo;
import com.keystow.app.page.PaginationUtil;
import com.keystow.app.repository.filter.GrupoFilterListDto;
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
public class GrupoQueriesImpl implements GrupoQueries {

	private final PaginationUtil paginationUtil;

	private final EntityManager entityManager;

	@Override
	@Transactional(readOnly = true)
	public Page<Grupo> filtro(GrupoFilterListDto grupoFilterListDto, Pageable pageable) {
		CriteriaBuilder criteriaBuilderGrupo = entityManager.getCriteriaBuilder();
		CriteriaQuery<Grupo> criteriaQueryGrupo = criteriaBuilderGrupo.createQuery(Grupo.class);
		Root<Grupo> rootGrupo = criteriaQueryGrupo.from(Grupo.class);
		criteriaQueryGrupo.select(rootGrupo); // EXPLICITAMENTE

		if (grupoFilterListDto.getDono() == null) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}

		Predicate[] predicatesGrupo = adicionarFiltro(grupoFilterListDto, criteriaBuilderGrupo, rootGrupo);
		criteriaQueryGrupo.where(predicatesGrupo);

		TypedQuery<Grupo> typedQueryGrupo = paginationUtil.prepare(entityManager, criteriaBuilderGrupo,
				criteriaQueryGrupo, rootGrupo, pageable);

		return new PageImpl<>(typedQueryGrupo.getResultList(), pageable, total(grupoFilterListDto));
	}

	private Predicate[] adicionarFiltro(GrupoFilterListDto grupoFilterListDto, CriteriaBuilder criteriaBuilderGrupo,
			Root<Grupo> rootGrupo) {

		List<Predicate> predicatesGrupo = new ArrayList<>();

		if (grupoFilterListDto.getDono() != null) {

			if (StringUtils.hasText(grupoFilterListDto.getDono().getId())) {
				predicatesGrupo.add(criteriaBuilderGrupo.equal(rootGrupo.get("dono").get("id"),
						grupoFilterListDto.getDono().getId()));
			}

			if (StringUtils.hasText(grupoFilterListDto.getNome())) {
				predicatesGrupo.add(criteriaBuilderGrupo.like(criteriaBuilderGrupo.lower(rootGrupo.get("nome")),
						"%" + grupoFilterListDto.getNome().toLowerCase() + "%"));
			}
		}
		return predicatesGrupo.toArray(new Predicate[0]);
	}

	private long total(GrupoFilterListDto grupoFilterListDto) {
		CriteriaBuilder criteriaBuilderGrupo = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQueryGrupo = criteriaBuilderGrupo.createQuery(Long.class);
		Root<Grupo> rootGrupo = criteriaQueryGrupo.from(Grupo.class);

		criteriaQueryGrupo.select(criteriaBuilderGrupo.count(rootGrupo));
		Predicate[] predicatesGrupo = adicionarFiltro(grupoFilterListDto, criteriaBuilderGrupo, rootGrupo);
		criteriaQueryGrupo.where(predicatesGrupo);

		return entityManager.createQuery(criteriaQueryGrupo).getSingleResult();
	}

}
