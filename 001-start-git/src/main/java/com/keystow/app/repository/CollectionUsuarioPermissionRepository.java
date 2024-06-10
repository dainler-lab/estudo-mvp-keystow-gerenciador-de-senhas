package com.keystow.app.repository;

import com.keystow.app.model.collection.CollectionUsuarioPermission;
import com.keystow.app.repository.helper.collection.CollectionQueries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionUsuarioPermissionRepository
		extends JpaRepository<CollectionUsuarioPermission, String>, CollectionQueries {

}
