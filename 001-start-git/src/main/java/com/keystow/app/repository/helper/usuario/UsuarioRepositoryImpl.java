package com.keystow.app.repository.helper.usuario;

import com.keystow.app.model.Grupo;
import com.keystow.app.model.Usuario;
import com.keystow.app.page.PaginationUtil;
import com.keystow.app.repository.filter.UsuarioFilterListDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
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
public class UsuarioRepositoryImpl implements UsuarioQueries {

	private final PaginationUtil paginationUtil;

	private final EntityManager entityManager;

	@Override
	@Transactional(readOnly = true)
	public Page<Usuario> filtro(UsuarioFilterListDto usuarioFilterListDto, Pageable pageable) {
		CriteriaBuilder criteriaBuilderUsuario = entityManager.getCriteriaBuilder();
		CriteriaQuery<Usuario> criteriaQueryUsuario = criteriaBuilderUsuario.createQuery(Usuario.class);
		Root<Usuario> rootUsuario = criteriaQueryUsuario.from(Usuario.class);
		criteriaQueryUsuario.select(rootUsuario); // EXPLICITAMENTE

		if (usuarioFilterListDto.getDono() == null) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}

		Predicate[] predicatesUsuario = adicionarFiltro(usuarioFilterListDto, criteriaBuilderUsuario, rootUsuario);
		criteriaQueryUsuario.where(predicatesUsuario);

		TypedQuery<Usuario> typedQueryUsuario = paginationUtil.prepare(entityManager, criteriaBuilderUsuario,
				criteriaQueryUsuario, rootUsuario, pageable);

		return new PageImpl<>(typedQueryUsuario.getResultList(), pageable, total(usuarioFilterListDto));
	}

	private Predicate[] adicionarFiltro(UsuarioFilterListDto usuarioFilterListDto,
			CriteriaBuilder criteriaBuilderUsuario, Root<Usuario> rootUsuario) {

		List<Predicate> predicatesUsuario = new ArrayList<>();

		if (usuarioFilterListDto.getDono() != null) {

			if (StringUtils.hasText(usuarioFilterListDto.getUsuarioAppId())) {
				predicatesUsuario.add(
						criteriaBuilderUsuario.notEqual(rootUsuario.get("id"), usuarioFilterListDto.getUsuarioAppId()));
			}

			if (StringUtils.hasText(usuarioFilterListDto.getDono().getId())) {
				predicatesUsuario.add(criteriaBuilderUsuario.equal(rootUsuario.get("dono").get("id"),
						usuarioFilterListDto.getDono().getId()));
			}

			if (StringUtils.hasText(usuarioFilterListDto.getUsuarioNome())) {
				predicatesUsuario.add(criteriaBuilderUsuario.like(criteriaBuilderUsuario.lower(rootUsuario.get("nome")),
						"%" + usuarioFilterListDto.getUsuarioNome().toLowerCase() + "%"));
			}

			if (StringUtils.hasText(usuarioFilterListDto.getEmail())) {
				predicatesUsuario
					.add(criteriaBuilderUsuario.like(criteriaBuilderUsuario.lower(rootUsuario.get("email")),
							"%" + usuarioFilterListDto.getEmail().toLowerCase() + "%"));
			}

			if (usuarioFilterListDto.getAtivo() != null) {
				predicatesUsuario
					.add(criteriaBuilderUsuario.equal(rootUsuario.get("ativo"), usuarioFilterListDto.getAtivo()));
			}

			if (usuarioFilterListDto.getGrupos() != null && !usuarioFilterListDto.getGrupos().isEmpty()) {
				for (String grupoId : usuarioFilterListDto.getGrupos()
					.stream()
					.map(Grupo::getId)
					.toArray(String[]::new)) {
					Join<Usuario, Grupo> joinUsuarioGrupo = rootUsuario.join("grupos", JoinType.INNER);
					predicatesUsuario.add(criteriaBuilderUsuario.equal(joinUsuarioGrupo.get("id"), grupoId));
				}
			}
		}
		return predicatesUsuario.toArray(new Predicate[0]);
	}

	private long total(UsuarioFilterListDto usuarioFilterListDto) {
		CriteriaBuilder criteriaBuilderUsuario = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQueryUsuario = criteriaBuilderUsuario.createQuery(Long.class);
		Root<Usuario> rootUsuario = criteriaQueryUsuario.from(Usuario.class);

		criteriaQueryUsuario.select(criteriaBuilderUsuario.count(rootUsuario));
		Predicate[] predicatesUsuario = adicionarFiltro(usuarioFilterListDto, criteriaBuilderUsuario, rootUsuario);
		criteriaQueryUsuario.where(predicatesUsuario);

		return entityManager.createQuery(criteriaQueryUsuario).getSingleResult();
	}

}
