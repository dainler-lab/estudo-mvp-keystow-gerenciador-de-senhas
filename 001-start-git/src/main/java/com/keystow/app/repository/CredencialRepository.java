package com.keystow.app.repository;

import com.keystow.app.model.Credencial;
import com.keystow.app.repository.helper.credencial.CredencialQueries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CredencialRepository extends JpaRepository<Credencial, String>, CredencialQueries {

	long countByIdIn(Collection<String> id);

	List<Credencial> findByIdIn(Collection<String> id);

	Optional<Credencial> findByIdAndDonoId(String id, String donoId);

	Optional<Credencial> findByIdAndDonoIdAndLixeiraTrue(String id, String donoId);

	List<Credencial> findAllByDonoIdAndLixeiraTrue(String donoId);

	// @formatter:off
	@Query("SELECT DISTINCT i FROM ItemDoCofre i " +
		   "LEFT JOIN i.collectionsList ic " +
		   "LEFT JOIN ic.collectionsUsuariosPermissionsList cup " +
		   "LEFT JOIN ic.collectionsGruposPermissionsList cgp " +
		   "LEFT JOIN cgp.grupo.usuarios gu " +
		   "WHERE (i.dono.id = :usuarioId " +
		   "OR cup.usuario.id = :usuarioId " +
		   "OR gu.id = :usuarioId) " +
		   "AND i.lixeira = false")
	Page<Credencial> findByDonoOrPermissionsAndNotInTrash(String usuarioId, Pageable pageable);

	@Query("SELECT DISTINCT i FROM ItemDoCofre i " +
		   "LEFT JOIN i.collectionsList ic " +
		   "LEFT JOIN ic.collectionsUsuariosPermissionsList cup " +
		   "LEFT JOIN ic.collectionsGruposPermissionsList cgp " +
		   "LEFT JOIN cgp.grupo.usuarios gu " +
		   "WHERE (i.dono.id = :usuarioId " +
		   "OR cup.usuario.id = :usuarioId " +
		   "OR gu.id = :usuarioId) " +
		   "AND i.lixeira = false")
	List<Credencial> findByDonoOrPermissionsAndNotInTrash(String usuarioId);

}
