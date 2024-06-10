package com.keystow.app.repository;

import com.keystow.app.model.SenhasCollection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SenhaRepository extends JpaRepository<SenhasCollection, String> {

	List<SenhasCollection> findAllByDonoId(String usuarioId, Sort sort);

	void deleteAllByDonoId(String usuarioId);

}
