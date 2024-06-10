package com.keystow.app.controller.form;

import com.keystow.app.model.Dono;
import com.keystow.app.model.Grupo;
import com.keystow.app.validation.email.EmailExisting;
import com.keystow.app.validation.password.PasswordsMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@EmailExisting(message = "E-mail já cadastrado, por favor informe outro")
@PasswordsMatch(message = "As senhas não coincidem. Certifique-se de digitar a mesma senha duas vezes")
public class UsuarioFormDto {

	private String id;

	@Size(min = 3, max = 300, message = "O campo nome deve ter entre 3 e 300 caracteres")
	private String usuarioNome;

	@Email(message = "O e-mail não é válido")
	private String email;

	@Size(min = 8, max = 200, message = "A senha deve ter entre 8 e 200 caracteres")
	@Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&' ()*+, -./:;<=>?@]_*|}~).{8,200}$",
			message = "A senha deve conter ao menos um dígito, uma letra minúscula, uma letra maiúscula e um caractere especial")
	private String senha;

	private String confirmacaoDeSenha;

	private Boolean ativo = true;

	private Dono dono;

	private List<Grupo> grupos;

	public boolean isNew() {
		return this.id == null || this.id.isBlank() || this.id.isEmpty();
	}

	@Override
	public String toString() {
		return "USUARIO FORM DTO: >>NOME: " + this.usuarioNome;
	}

}
