package com.keystow.app.page;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PaginationUtil {

	public <T> TypedQuery<T> prepare(EntityManager manager, CriteriaBuilder builder, CriteriaQuery<T> criteriaQuery,
			Root<T> root, Pageable pageable) {
		Sort sort = pageable.getSort();
		if (sort.isSorted()) {
			for (Sort.Order order : sort) {
				String property = order.getProperty();
				Order jpaOrder = order.isAscending() ? builder.asc(root.get(property))
						: builder.desc(root.get(property));
				criteriaQuery.orderBy(jpaOrder);
			}
		}

		TypedQuery<T> typedQuery = manager.createQuery(criteriaQuery);

		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistrosPorPagina;

		typedQuery.setFirstResult(primeiroRegistro);
		typedQuery.setMaxResults(totalRegistrosPorPagina);

		return typedQuery;
	}

}
