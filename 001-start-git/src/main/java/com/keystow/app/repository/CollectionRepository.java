package com.keystow.app.repository;

import com.keystow.app.model.ItemDoCofre;
import com.keystow.app.model.collection.CollectionDeItensDoCofre;
import com.keystow.app.model.collection.CollectionGrupoPermission;
import com.keystow.app.model.collection.CollectionUsuarioPermission;
import com.keystow.app.repository.helper.collection.CollectionQueries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<CollectionDeItensDoCofre, String>, CollectionQueries {

    Optional<CollectionDeItensDoCofre> findByNomeIgnoreCaseAndDonoId(String nome, String donoId);

    Optional<CollectionDeItensDoCofre> findByIdAndDonoId(String id, String donoId);

    // @formatter:off

    // Altere o tipo de retorno para List<CollectionUsuarioPermission>
    @Query("SELECT c.collectionsUsuariosPermissionsList FROM CollectionDeItensDoCofre c " +
           "WHERE c.id = :id AND c.dono.id = :donoId")
    List<CollectionUsuarioPermission> getCollectionUsuarioPermissionsByIdAndDonoId(String id, String donoId);

    // Altere o tipo de retorno para List<CollectionGrupoPermission>
    @Query("SELECT c.collectionsGruposPermissionsList FROM CollectionDeItensDoCofre c " +
           "WHERE c.id = :id AND c.dono.id = :donoId")
    List<CollectionGrupoPermission> getCollectionGrupoPermissionsByIdAndDonoId(String id, String donoId);

    // Altere o tipo de retorno para List<ItemDoCofre>
    @Query("SELECT c.itensList FROM CollectionDeItensDoCofre c " +
           "WHERE c.id = :id AND c.dono.id = :donoId")
    List<ItemDoCofre> getItensByIdAndDonoId(String id, String donoId);

}
