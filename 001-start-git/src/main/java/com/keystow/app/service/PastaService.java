package com.keystow.app.service;

import com.keystow.app.controller.form.PastaFormDto;
import com.keystow.app.exception.EntityNotFoundBusinessException;
import com.keystow.app.exception.ImpossivelExcluirEntidadeForeignKeyConstraintException;
import com.keystow.app.exception.NomeJaExisteException;
import com.keystow.app.model.Pasta;
import com.keystow.app.model.Usuario;
import com.keystow.app.repository.ItemRepository;
import com.keystow.app.repository.PastaRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PastaService {

	private final ModelMapper modelMapper;

	private final PastaRepository pastaRepository;

	private final UsuarioService usuarioService;

	private final ItemRepository itemRepository;

	public Pasta toModel(PastaFormDto pastaFormDto) {
		return modelMapper.map(pastaFormDto, Pasta.class);
	}

	public PastaFormDto toDto(Pasta pasta) {
		return modelMapper.map(pasta, PastaFormDto.class);
	}

	@Transactional(readOnly = true)
	public PastaFormDto getPastaById(String id) {
		return toDto(pastaRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundBusinessException("Pasta não encontrada")));
	}

	@Transactional(readOnly = true)
	public Pasta getPastaByIdAndDonoId(String id, String donoId) {
		return pastaRepository.findByIdAndDonoId(id, donoId)
			.orElseThrow(() -> new EntityNotFoundBusinessException("Pasta não encontrada"));
	}

	@Transactional
	public PastaFormDto salvarPastaPorDono(PastaFormDto pastaFormDto, String donoId) {
		Pasta pasta = toModel(pastaFormDto);
		Usuario usuario = usuarioService.getUsuarioById(donoId);

		Optional<Pasta> pastaOptional = pastaRepository.findByNomeIgnoreCaseAndDonoId(pasta.getNome(), usuario.getId());
		if (pastaOptional.isPresent() && !pastaOptional.get().equals(pasta)) {
			throw new NomeJaExisteException("O nome da pasta já existe, tente outro novamente.");
		}

		pasta.setDono(usuario);
		return toDto(pastaRepository.save(pasta));
	}

	@Transactional
	public void excluirPastaPorDono(String id, String donoId) {
		Pasta pasta = getPastaByIdAndDonoId(id, donoId);
		long itemCount = itemRepository.countByPastaId(pasta.getId());

		if (itemCount > 0) {
			throw new ImpossivelExcluirEntidadeForeignKeyConstraintException(
					"Não é possível excluir a pasta porque ela está sendo referenciada por um ou mais itens do cofre");
		}

		pastaRepository.delete(pasta);
	}

}
