package com.keystow.app.repository;

import com.keystow.app.controller.list.CollectionUsuarioPermissionDto;
import com.keystow.app.controller.list.GrupoUsuarioPesquisaDto;
import com.keystow.app.model.Usuario;
import com.keystow.app.repository.helper.usuario.UsuarioQueries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String>, UsuarioQueries {

    Optional<Usuario> findById(String id);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByIdIn(Collection<String> id);

    Optional<Usuario> findByEmailIgnoreCaseAndAtivoTrue(String email);

    // @formatter:off
    @Query("SELECT new com.keystow.app.controller.list.CollectionUsuarioPermissionDto(u.id, u.nome, u.email) " +
           "FROM Usuario u WHERE (LOWER(u.nome) LIKE LOWER(concat(:nomeOuEmail, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(concat(:nomeOuEmail, '%'))) AND u.id != :usuarioId")
    List<CollectionUsuarioPermissionDto> findByNomeOrEmailStartingWithIgnoreCaseAndNotUsuarioId(String nomeOuEmail, String usuarioId);

	@Query("SELECT new com.keystow.app.controller.list.GrupoUsuarioPesquisaDto(u.id, u.nome, u.email) " +
		   "FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(concat(:nomeOuEmail, '%')) OR " +
		   "LOWER(u.email) LIKE LOWER(concat(:nomeOuEmail, '%'))")
	List<GrupoUsuarioPesquisaDto> findByNomeOrEmailStartingWithIgnoreCase(String nomeOuEmail);

}
