package com.keystow.app.service;

import com.keystow.app.controller.form.CredencialFormDto;
import com.keystow.app.exception.EntityNotFoundBusinessException;
import com.keystow.app.mapper.CredencialMapper;
import com.keystow.app.model.Credencial;
import com.keystow.app.repository.CredencialRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class CredencialService {

	private final CredencialRepository credencialRepository;

	private final CredencialMapper credencialMapper;

	public CredencialFormDto toDto(Credencial credencial) {
		return credencialMapper.toFormDto(credencial);
	}

	public Credencial toModel(CredencialFormDto credencialFormDto) {
		return credencialMapper.toModel(credencialFormDto);
	}

	@Transactional(readOnly = true)
	public Credencial getCredencialById(String id) {
		return credencialRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundBusinessException("Credencial não encontrada"));
	}

	@Transactional
	public void save(Credencial credencial) {
		if (credencial.isNew()) {
			credencial.setCriadoEm(LocalDateTime.now());
			credencial.setLixeira(false);
		}
		else {
			Credencial credencialSaved = getCredencialById(credencial.getId());
			credencial.setCriadoEm(credencialSaved.getCriadoEm());
			credencial.setLixeira(credencialSaved.getLixeira());
		}
		credencial.setAtualizadoEm(LocalDateTime.now());
		credencialRepository.save(credencial);
	}

	@Transactional
	public void alterarFavoritosSelecionados(Collection<String> ids, Boolean favorito) {
		// Verifica se todos os IDs existem
		long count = credencialRepository.countByIdIn(ids);
		if (count != ids.size()) {
			throw new EntityNotFoundBusinessException("Uma ou mais credenciais não foram encontradas.");
		}
		// Se todos os IDs existirem, executa a alteração de favoritos
		credencialRepository.findByIdIn(ids).forEach(c -> {
			c.setFavorito(favorito);
			c.setAtualizadoEm(LocalDateTime.now());
		});
	}

	@Transactional
	public void restaurarSelecionadosLixeira(List<String> ids, String usuarioId) {
		ids.forEach(id -> {
			credencialRepository.findByIdAndDonoId(id, usuarioId).ifPresentOrElse(credencial -> {
				credencial.setLixeira(false);
				credencial.setAtualizadoEm(LocalDateTime.now());
				credencialRepository.save(credencial);
			}, () -> {
				throw new EntityNotFoundBusinessException("Credencial não encontrado");
			});
		});
	}

	@Transactional
	public void restaurarTodosAllLixeira(String usuarioId) {
		List<Credencial> credenciais = credencialRepository.findAllByDonoIdAndLixeiraTrue(usuarioId);
		if (credenciais.isEmpty()) {
			throw new EntityNotFoundBusinessException("Credencial não encontrado");
		}
		credenciais.forEach(credencial -> {
			credencial.setLixeira(false);
			credencial.setAtualizadoEm(LocalDateTime.now());
			credencialRepository.save(credencial);
		});
	}

	@Transactional
	public void excluirItensSelecionadosParaLixeira(String[] ids) {
		Arrays.stream(ids).forEach(id -> {
			credencialRepository.findById(id).ifPresentOrElse(credencial -> {
				credencial.setLixeira(true);
				credencial.setAtualizadoEm(LocalDateTime.now());
				credencialRepository.save(credencial);
			}, () -> {
				throw new EntityNotFoundBusinessException("Credencial não encontrado");
			});
		});
	}

	@Transactional
	public void esvaziarLixeiraPermanentemente(String usuarioId) {
		credencialRepository.findAllByDonoIdAndLixeiraTrue(usuarioId).forEach(credencial -> {
			credencial.getCollectionsList().forEach(collection -> collection.getItensList().remove(credencial));
			credencialRepository.delete(credencial);
		});
	}

	@Transactional
	public void excluirSelecionadosPermanentemente(List<String> ids, String usuarioId) {
		ids.forEach(id -> {
			credencialRepository.findByIdAndDonoIdAndLixeiraTrue(id, usuarioId).ifPresentOrElse(credencial -> {
				credencial.getCollectionsList().forEach(collection -> collection.getItensList().remove(credencial));
				credencialRepository.delete(credencial);
			}, () -> {
				throw new EntityNotFoundBusinessException("Credencial não encontrado");
			});
		});
	}

}
