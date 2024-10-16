package com.keystow.app.repository.helper.credencial;

import com.keystow.app.model.*;
import com.keystow.app.model.collection.CollectionDeItensDoCofre;
import com.keystow.app.model.collection.CollectionDeItensDoCofre_;
import com.keystow.app.model.collection.CollectionGrupoPermission;
import com.keystow.app.model.collection.CollectionUsuarioPermission;
import com.keystow.app.page.PaginationUtil;
import com.keystow.app.repository.filter.CredencialFilterListDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class CredencialRepositoryImpl implements CredencialQueries {

    private static final Logger logger = LoggerFactory.getLogger(CredencialRepositoryImpl.class);

    private final PaginationUtil paginationUtil;

    private final EntityManager entityManager;

    private record Joins(Join<Credencial, CollectionDeItensDoCofre> collectionJoin, Join<CollectionDeItensDoCofre, CollectionUsuarioPermission> cupJoin,
                         Join<CollectionDeItensDoCofre, CollectionGrupoPermission> cgpJoin, Join<CollectionGrupoPermission, Grupo> grupoJoin,
                         Join<Grupo, Usuario> guJoin) {}

    @Override
    @Transactional(readOnly = true)
    public Page<Credencial> filtro(CredencialFilterListDto filterDto, Pageable pageable) {
        CriteriaQuery<Credencial> criteriaQuery = createBaseCriteriaQuery(filterDto, false);
        TypedQuery<Credencial> typedQuery = createTypedQuery(criteriaQuery, pageable);

        List<Credencial> results = typedQuery.getResultList();
        //        logger.debug("Number of results in filtro: {}", results.size());

        return new PageImpl<>(results, pageable, total(filterDto, false));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Credencial> filtroDonoAndLixeira(CredencialFilterListDto filterDto, Pageable pageable) {
        CriteriaQuery<Credencial> criteriaQuery = createBaseCriteriaQuery(filterDto, true);
        TypedQuery<Credencial> typedQuery = createTypedQuery(criteriaQuery, pageable);

        List<Credencial> results = typedQuery.getResultList();
        //        logger.debug("Number of results in filtroDonoAndLixeira: {}", results.size());

        return new PageImpl<>(results, pageable, total(filterDto, true));
    }

    @Override
    @Transactional(readOnly = true)
    public Credencial getCredencialComCamposPersonalizados(String id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Credencial> query = cb.createQuery(Credencial.class);
        Root<Credencial> root = query.from(Credencial.class);

        root.fetch(Credencial_.camposPersonalizados, JoinType.LEFT);

        query.select(root).where(cb.equal(root.get(Credencial_.id), id)).distinct(true);

        try {
            return entityManager.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            logger.warn("No Credencial found with id: {}", id);
            return null;
        }
    }

    private CriteriaQuery<Credencial> createBaseCriteriaQuery(CredencialFilterListDto filterDto, boolean isLixeira) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Credencial> query = cb.createQuery(Credencial.class);
        Root<Credencial> root = query.from(Credencial.class);

        Joins joins = createJoins(root);
        Predicate combinedCondition = createCombinedCondition(cb, root, joins, filterDto, isLixeira);

        return query.where(combinedCondition).select(root).distinct(true);
    }

    private Joins createJoins(Root<Credencial> root) {
        Join<Credencial, CollectionDeItensDoCofre> collectionJoin = root.join(Credencial_.collectionsList, JoinType.LEFT);
        Join<CollectionDeItensDoCofre, CollectionUsuarioPermission> cupJoin =
                collectionJoin.join(CollectionDeItensDoCofre_.collectionsUsuariosPermissionsList, JoinType.LEFT);
        Join<CollectionDeItensDoCofre, CollectionGrupoPermission> cgpJoin =
                collectionJoin.join(CollectionDeItensDoCofre_.collectionsGruposPermissionsList, JoinType.LEFT);
        Join<CollectionGrupoPermission, Grupo> grupoJoin = cgpJoin.join("grupo", JoinType.LEFT);
        Join<Grupo, Usuario> guJoin = grupoJoin.join("usuarios", JoinType.LEFT);

        return new Joins(collectionJoin, cupJoin, cgpJoin, grupoJoin, guJoin);
    }

    private Predicate createCombinedCondition(CriteriaBuilder cb, Root<Credencial> root, Joins joins, CredencialFilterListDto filterDto,
                                              boolean isLixeira) {
        Predicate donoCondition = cb.equal(root.get(Credencial_.dono).get(Dono_.id), filterDto.getDono().getId());
        Predicate lixeiraCondition = cb.equal(root.get(Credencial_.lixeira), isLixeira);

        if (isLixeira) {
            return cb.and(donoCondition, lixeiraCondition);
        } else {
            Predicate usuarioCondition = cb.equal(joins.cupJoin.get("usuario").get("id"), filterDto.getDono().getId());
            Predicate grupoCondition = cb.equal(joins.guJoin.get("id"), filterDto.getDono().getId());
            return cb.and(cb.or(donoCondition, usuarioCondition, grupoCondition), lixeiraCondition);
        }
    }

    private TypedQuery<Credencial> createTypedQuery(CriteriaQuery<Credencial> criteriaQuery, Pageable pageable) {
        Root<Credencial> root = criteriaQuery.from(Credencial.class);
        return paginationUtil.prepare(entityManager, entityManager.getCriteriaBuilder(), criteriaQuery, root, pageable);
    }

    private long total(CredencialFilterListDto filterDto, boolean isLixeira) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Credencial> root = query.from(Credencial.class);

        Joins joins = createJoins(root);
        Predicate combinedCondition = createCombinedCondition(cb, root, joins, filterDto, isLixeira);
        List<Predicate> predicates = new ArrayList<>(Collections.singletonList(combinedCondition));
        predicates.addAll(adicionarDemaisFiltros(filterDto, cb, root));

        query.select(cb.count(root)).where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getSingleResult();
    }

    private List<Predicate> adicionarDemaisFiltros(CredencialFilterListDto filterDto, CriteriaBuilder cb, Root<Credencial> root) {

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(filterDto.getTipoDeItemDoCofre()).ifPresent(tipo -> predicates.add(cb.equal(root.get(Credencial_.tipoDeItemDoCofre), tipo)));

        Optional.ofNullable(filterDto.getNome()).filter(StringUtils::hasText).ifPresent(
                nome -> predicates.add(cb.like(cb.lower(root.get(Credencial_.nome)), "%" + nome.toLowerCase() + "%")));

        Optional.ofNullable(filterDto.getPasta()).ifPresent(pasta -> predicates.add(cb.equal(root.get(Credencial_.pasta), pasta)));

        return predicates;
    }
}
