package com.keystow.app.service;

import com.keystow.app.controller.form.CollectionFormDto;
import com.keystow.app.exception.EntityNotFoundBusinessException;
import com.keystow.app.exception.NomeJaExisteException;
import com.keystow.app.mapper.CollectionItemDoCofreMapper;
import com.keystow.app.model.collection.CollectionDeItensDoCofre;
import com.keystow.app.repository.CollectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CollectionService {

    private final CollectionItemDoCofreMapper collectionItemDoCofreMapper;

    private final CollectionRepository collectionRepository;

    private final UsuarioService usuarioService;

    public CollectionDeItensDoCofre toModel(CollectionFormDto collectionFormDto) {
        return collectionItemDoCofreMapper.toModel(collectionFormDto);
    }

    public CollectionFormDto toFormDto(CollectionDeItensDoCofre collectionDeItensDoCofre) {
        return collectionItemDoCofreMapper.toFormDto(collectionDeItensDoCofre);
    }

    @Transactional(readOnly = true)
    public CollectionDeItensDoCofre getCollectionByIdAndDonoId(String id, String donoId) {
        return collectionRepository.findByIdAndDonoId(id, donoId).orElseThrow(() -> new EntityNotFoundBusinessException("Coleção não encontrada"));
    }

    @Transactional
    public void salvarCollectionPorDono(CollectionFormDto collectionFormDto, String donoId) {

        CollectionDeItensDoCofre collection = toModel(collectionFormDto);

        Optional<CollectionDeItensDoCofre> cOptional = collectionRepository.findByNomeIgnoreCaseAndDonoId(collection.getNome(), donoId);
        if (cOptional.isPresent() && !cOptional.get().equals(collection)) {
            throw new NomeJaExisteException("O nome da coleção já existe, tente outro novamente.");
        }

        if (collection.isNew()) {
            collection.setCriadoEm(LocalDateTime.now());
        } else {
            CollectionDeItensDoCofre collectionSaved = getCollectionByIdAndDonoId(collection.getId(), donoId);
            collection.setCriadoEm(collectionSaved.getCriadoEm());
        }

        collection.setDono(usuarioService.getUsuarioById(donoId));
        collection.setAtualizadoEm(LocalDateTime.now());
        collectionRepository.save(collection);
    }

    @Transactional
    public void excluirCollectionPorDono(String id, String donoId) {
        CollectionDeItensDoCofre collectionDeItensDoCofre = getCollectionByIdAndDonoId(id, donoId);
        collectionRepository.delete(collectionDeItensDoCofre);
    }

}
