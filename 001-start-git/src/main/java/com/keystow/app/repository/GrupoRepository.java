package com.keystow.app.repository;

import com.keystow.app.controller.list.CollectionGrupoPermissionDto;
import com.keystow.app.model.Grupo;
import com.keystow.app.repository.helper.grupo.GrupoQueries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, String>, GrupoQueries {

    Optional<Grupo> findByNomeIgnoreCaseAndDonoId(String nome, String donoId);

    Optional<Grupo> findByIdAndDonoId(String id, String donoId);

    List<Grupo> findAllByDonoId(String donoId);

    // @formatter:off
	@Query("SELECT new com.keystow.app.controller.list.CollectionGrupoPermissionDto(g.id, g.nome) " +
			"FROM Grupo g WHERE LOWER(g.nome) LIKE LOWER(concat(:nome, '%')) AND g.dono.id = :donoId")
	List<CollectionGrupoPermissionDto> findByNomeStartingWithIgnoreCaseAndDonoId(String nome, String donoId);

	// Grupo com usuários
	@Query("SELECT g FROM Grupo g LEFT JOIN FETCH g.usuarios WHERE g.id = :id AND g.dono.id = :donoId")
	Optional<Grupo> findByIdAndDonoIdWithUsuarios(String id, String donoId);

}
