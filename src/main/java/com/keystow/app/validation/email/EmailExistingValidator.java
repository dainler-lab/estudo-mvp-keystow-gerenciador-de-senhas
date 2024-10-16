package com.keystow.app.validation.email;

import com.keystow.app.controller.form.UsuarioFormDto;
import com.keystow.app.mapper.UsuarioMapper;
import com.keystow.app.model.Usuario;
import com.keystow.app.repository.UsuarioRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class EmailExistingValidator implements ConstraintValidator<EmailExisting, UsuarioFormDto> {

	private final UsuarioRepository usuarioRepository;

	private final UsuarioMapper usuarioMapper;

	public void initialize(EmailExisting constraint) {
	}

	public boolean isValid(UsuarioFormDto formDto, ConstraintValidatorContext context) {
		Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(formDto.getEmail());
		if (usuarioExistente.isPresent() && !usuarioExistente.get().equals(usuarioMapper.toModel(formDto))) {
			// throw new EmailJaExisteException("E-mail já cadastrado");
			context.disableDefaultConstraintViolation();
			String mensagem = context.getDefaultConstraintMessageTemplate();
			context.buildConstraintViolationWithTemplate(mensagem).addPropertyNode("email").addConstraintViolation();
			return false;
		}
		return true;
	}

}
