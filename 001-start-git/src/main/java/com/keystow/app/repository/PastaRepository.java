package com.keystow.app.repository;

import com.keystow.app.model.Pasta;
import com.keystow.app.repository.helper.pasta.PastaQueries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PastaRepository extends JpaRepository<Pasta, String>, PastaQueries {

	Optional<Pasta> findByIdAndDonoId(String id, String donoId);

	Optional<Pasta> findByNomeIgnoreCaseAndDonoId(String nome, String donoId);

	List<Pasta> findAllByDonoId(String donoId);

}
