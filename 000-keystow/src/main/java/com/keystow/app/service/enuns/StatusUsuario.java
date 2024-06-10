package com.keystow.app.service.enuns;

import com.keystow.app.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@AllArgsConstructor
public enum StatusUsuario {

	ATIVAR("Ativado") {
		@Override
		public void executar(Collection<String> ids, UsuarioRepository usuarioRepository) {
			usuarioRepository.findByIdIn(ids).forEach(u -> {
				u.setAtivo(true);
				u.setAtualizadoEm(LocalDateTime.now());
			});
		}
	},

	DESATIVAR("Desativado") {
		@Override
		public void executar(Collection<String> ids, UsuarioRepository usuarioRepository) {
			usuarioRepository.findByIdIn(ids).forEach(u -> {
				u.setAtivo(false);
				u.setAtualizadoEm(LocalDateTime.now());
			});
		}
	};

	private final String nome;

	public abstract void executar(Collection<String> ids, UsuarioRepository usuarioRepository);

}
