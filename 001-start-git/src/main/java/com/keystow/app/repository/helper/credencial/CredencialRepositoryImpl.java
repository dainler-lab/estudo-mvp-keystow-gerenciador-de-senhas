package com.keystow.app.repository.helper.credencial;

import com.keystow.app.model.Credencial;
import com.keystow.app.model.Credencial_;
import com.keystow.app.model.Dono_;
import com.keystow.app.model.Usuario;
import com.keystow.app.model.collection.CollectionDeItensDoCofre;
import com.keystow.app.model.collection.CollectionDeItensDoCofre_;
import com.keystow.app.model.collection.CollectionGrupoPermission;
import com.keystow.app.model.collection.CollectionUsuarioPermission;
import com.keystow.app.page.PaginationUtil;
import com.keystow.app.repository.filter.CredencialFilterListDto;
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
public class CredencialRepositoryImpl implements CredencialQueries {

    private final PaginationUtil paginationUtil;

    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Page<Credencial> filtro(CredencialFilterListDto credencialFilterListDto, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Credencial> criteriaQuery = criteriaBuilder.createQuery(Credencial.class);
        Root<Credencial> rootCredencial = criteriaQuery.from(Credencial.class);

        criteriaQuery.select(rootCredencial); // EXPLICITAMENTE

//        @Query("SELECT DISTINCT i FROM ItemDoCofre i " +
//               "LEFT JOIN i.collectionsList ic " +
//               "LEFT JOIN ic.collectionsUsuariosPermissionsList cup " +
//               "LEFT JOIN ic.collectionsGruposPermissionsList cgp " +
//               "LEFT JOIN cgp.grupo.usuarios gu " +
//               "WHERE (i.dono.id = :usuarioId " +
//               "OR cup.usuario.id = :usuarioId " +
//               "OR gu.id = :usuarioId) " +
//               "AND i.lixeira = false")

        Join<Credencial, CollectionDeItensDoCofre> credencialCollectionJoin = rootCredencial.join(Credencial_.collectionsList, JoinType.LEFT);
        Join<CollectionDeItensDoCofre, CollectionUsuarioPermission> cupJoin =
                credencialCollectionJoin.join(CollectionDeItensDoCofre_.collectionsUsuariosPermissionsList, JoinType.LEFT);
        Join<CollectionDeItensDoCofre, CollectionGrupoPermission> cgpJoin =
                credencialCollectionJoin.join(CollectionDeItensDoCofre_.collectionsGruposPermissionsList, JoinType.LEFT);
        Join<CollectionGrupoPermission, Usuario> guJoin = cgpJoin.join("grupo").join("usuarios");

        Predicate donoCondition = criteriaBuilder.equal(rootCredencial.get(Credencial_.dono).get(Dono_.id), credencialFilterListDto.getDono().getId());
        Predicate usuarioCondition = criteriaBuilder.equal(cupJoin.get("usuario").get("id"), credencialFilterListDto.getDono().getId());
//      Predicate usuarioCondition = criteriaBuilder.equal(cupJoin.get(CollectionUsuarioPermission_.usuario).get(Usuario_.id),
//                                                                     credencialFilterListDto.getDono().getId());
        Predicate grupoCondition = criteriaBuilder.equal(guJoin.get("id"), credencialFilterListDto.getDono().getId());
        Predicate lixeiraCondition = criteriaBuilder.equal(rootCredencial.get(Credencial_.lixeira), false);

        Predicate combinedCondition = criteriaBuilder.and(criteriaBuilder.or(donoCondition, usuarioCondition, grupoCondition), lixeiraCondition);
        criteriaQuery.select(rootCredencial).distinct(true).where(combinedCondition);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(combinedCondition);
        predicates.addAll(adicionarDemaisFiltros(credencialFilterListDto, criteriaBuilder, rootCredencial));

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Credencial> typedQueryCredencial = paginationUtil.prepare(entityManager, criteriaBuilder, criteriaQuery, rootCredencial, pageable);

        return new PageImpl<>(typedQueryCredencial.getResultList(), pageable, total(credencialFilterListDto));
    }

    // Método auxiliar para contar o total de registros
    private long total(CredencialFilterListDto credencialFilterListDto) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Credencial> rootCredencial = criteriaQuery.from(Credencial.class);

        Join<Credencial, CollectionDeItensDoCofre> credencialCollectionJoin = rootCredencial.join("collectionsList", JoinType.LEFT);
        Join<CollectionDeItensDoCofre, CollectionUsuarioPermission> cupJoin =
                credencialCollectionJoin.join("collectionsUsuariosPermissionsList", JoinType.LEFT);
        Join<CollectionDeItensDoCofre, CollectionGrupoPermission> cgpJoin =
                credencialCollectionJoin.join("collectionsGruposPermissionsList", JoinType.LEFT);
        Join<CollectionGrupoPermission, Usuario> guJoin = cgpJoin.join("grupo").join("usuarios");

        Predicate donoCondition = criteriaBuilder.equal(rootCredencial.get("dono").get("id"), credencialFilterListDto.getDono().getId());
        Predicate usuarioCondition = criteriaBuilder.equal(cupJoin.get("usuario").get("id"), credencialFilterListDto.getDono().getId());
        Predicate grupoCondition = criteriaBuilder.equal(guJoin.get("id"), credencialFilterListDto.getDono().getId());
        Predicate lixeiraCondition = criteriaBuilder.equal(rootCredencial.get("lixeira"), false);

        Predicate combinedCondition = criteriaBuilder.and(criteriaBuilder.or(donoCondition, usuarioCondition, grupoCondition), lixeiraCondition);

        criteriaQuery.select(criteriaBuilder.count(rootCredencial));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(combinedCondition);
        predicates.addAll(adicionarDemaisFiltros(credencialFilterListDto, criteriaBuilder, rootCredencial));

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    // Método auxiliar para adicionar os demais filtros
    private List<Predicate> adicionarDemaisFiltros(CredencialFilterListDto credencialFilterListDto, CriteriaBuilder criteriaBuilder,
                                                   Root<Credencial> rootCredencial) {
        List<Predicate> predicates = new ArrayList<>();

        if (credencialFilterListDto.getLixeira() != null) {
            predicates.add(criteriaBuilder.equal(rootCredencial.get(Credencial_.lixeira), credencialFilterListDto.getLixeira()));
        }

        if (credencialFilterListDto.getTipoDeItemDoCofre() != null) {
            predicates.add(criteriaBuilder.equal(rootCredencial.get(Credencial_.tipoDeItemDoCofre), credencialFilterListDto.getTipoDeItemDoCofre()));
        }

        if (StringUtils.hasText(credencialFilterListDto.getNome())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(rootCredencial.get(Credencial_.nome)),
                                                "%" + credencialFilterListDto.getNome().toLowerCase() + "%"));
        }

        if (credencialFilterListDto.getPasta() != null) {
            predicates.add(criteriaBuilder.equal(rootCredencial.get(Credencial_.pasta), credencialFilterListDto.getPasta()));
        }

        return predicates;
    }

    public Credencial getCredencialComCamposPersonalizados(String id) {
        CriteriaBuilder criteriaBuilderCredencial = entityManager.getCriteriaBuilder();
        CriteriaQuery<Credencial> criteriaQueryCredencial = criteriaBuilderCredencial.createQuery(Credencial.class);
        Root<Credencial> rootCredencial = criteriaQueryCredencial.from(Credencial.class);
        criteriaQueryCredencial.select(rootCredencial); // EXPLICITAMENTE

        rootCredencial.fetch(Credencial_.camposPersonalizados, JoinType.LEFT); // fetch == JOIN
//		criteriaQueryCredencial.where(criteriaBuilderCredencial.equal(rootCredencial.get("id"), id));
        criteriaQueryCredencial.where(criteriaBuilderCredencial.equal(rootCredencial.get(Credencial_.id), id));
        criteriaQueryCredencial.distinct(true);
        return entityManager.createQuery(criteriaQueryCredencial).getSingleResult();
    }

    @Override
    public Page<Credencial> filtroDonoAndLixeira(CredencialFilterListDto credencialFilterListDto, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Credencial> criteriaQuery = criteriaBuilder.createQuery(Credencial.class);
        Root<Credencial> rootCredencial = criteriaQuery.from(Credencial.class);

        criteriaQuery.select(rootCredencial); // EXPLICITAMENTE

        List<Predicate> predicates = new ArrayList<>();
        Predicate donoCondition = criteriaBuilder.equal(rootCredencial.get(Credencial_.dono).get(Dono_.id), credencialFilterListDto.getDono().getId());
        Predicate lixeiraCondition = criteriaBuilder.equal(rootCredencial.get(Credencial_.lixeira), true);
        predicates.add(donoCondition);
        predicates.add(lixeiraCondition);
        predicates.addAll(adicionarDemaisFiltros(credencialFilterListDto, criteriaBuilder, rootCredencial));

        criteriaQuery.select(rootCredencial).distinct(true).where(criteriaBuilder.and(donoCondition, lixeiraCondition));

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Credencial> typedQueryCredencial = paginationUtil.prepare(entityManager, criteriaBuilder, criteriaQuery, rootCredencial, pageable);

        return new PageImpl<>(typedQueryCredencial.getResultList(), pageable, totalDonoAndLixeira(credencialFilterListDto));
    }

    private long totalDonoAndLixeira(CredencialFilterListDto credencialFilterListDto) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Credencial> rootCredencial = criteriaQuery.from(Credencial.class);

        Predicate donoCondition = criteriaBuilder.equal(rootCredencial.get(Credencial_.dono).get(Dono_.id), credencialFilterListDto.getDono().getId());
        Predicate lixeiraCondition = criteriaBuilder.equal(rootCredencial.get(Credencial_.lixeira), true);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(donoCondition);
        predicates.add(lixeiraCondition);
        predicates.addAll(adicionarDemaisFiltros(credencialFilterListDto, criteriaBuilder, rootCredencial));

        criteriaQuery.select(criteriaBuilder.count(rootCredencial)).where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

}
