package com.keystow.app.repository;

import com.keystow.app.controller.list.ItemCollectionPesquisaDto;
import com.keystow.app.model.ItemDoCofre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemDoCofre, String> {

	long countByPastaId(String id);

	boolean existsByDonoId(String id);

	// @formatter:off
	@Query("SELECT new com.keystow.app.controller.list.ItemCollectionPesquisaDto(i.id, i.nome) " +
			"FROM ItemDoCofre i WHERE LOWER(i.nome) LIKE LOWER(concat(:nome, '%')) AND i.dono.id = :donoId")
	List<ItemCollectionPesquisaDto> findByNomeStartingWithIgnoreCaseAndDonoId(String nome, String donoId);

}
