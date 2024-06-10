package com.keystow.app.service;

import com.keystow.app.controller.form.GeradorDeSenhaFormRequestDto;
import com.keystow.app.model.SenhasCollection;
import com.keystow.app.model.Usuario;
import com.keystow.app.repository.SenhaRepository;
import com.keystow.app.util.GeradorDeSenhaUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class GeradorDeSenhaService {

	private final SenhaRepository senhaRepository;

	private final UsuarioService usuarioService;

	private final GeradorDeSenhaUtil geradorDeSenhaUtil;

	@Transactional(readOnly = true)
	public List<SenhasCollection> buscarSenhasPorCriadoEmDescAndUsuario(String usuarioId) {
		return senhaRepository.findAllByDonoId(usuarioId, Sort.by(Sort.Direction.DESC, "criadoEm"));
	}

	@Transactional
	public String gerarSenhaPorUsuario(GeradorDeSenhaFormRequestDto geradorDeSenhaFormRequestDto, String donoId) {

		Usuario usuario = usuarioService.getUsuarioById(donoId);

		String senhaGerada = geradorDeSenhaUtil.gerar(geradorDeSenhaFormRequestDto.getTamanho(),
				geradorDeSenhaFormRequestDto.getMaiusculas(), geradorDeSenhaFormRequestDto.getMinusculas(),
				geradorDeSenhaFormRequestDto.getNumeros(), geradorDeSenhaFormRequestDto.getEspeciais(),
				geradorDeSenhaFormRequestDto.getEvitarAmbiguidade(), geradorDeSenhaFormRequestDto.getNumerosMinimos(),
				geradorDeSenhaFormRequestDto.getEspeciaisMinimos());

		SenhasCollection senhasCollectionEntity = new SenhasCollection();
		senhasCollectionEntity.setDono(usuario);
		senhasCollectionEntity.setValor(senhaGerada);
		senhasCollectionEntity.setCriadoEm(LocalDateTime.now());
		senhaRepository.save(senhasCollectionEntity);
		return senhaGerada;
	}

	@Transactional
	public void deletarTodasSenhasPorUsuario(String usuarioId) {
		senhaRepository.deleteAllByDonoId(usuarioId);
	}

	// public List<SenhasCollection> buscarSenhasPorCriadoEmDesc() {
	// return senhaRepository.findAll(Sort.by(Sort.Direction.DESC, "criadoEm"));
	// }

}
