package com.keystow.app.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@AllArgsConstructor
public class GeradorDeSenhaUtil {

	private static final SecureRandom secureRandom = new SecureRandom();

	public String gerar(int length, boolean useUppercase, boolean useLowercase, boolean useDigits, boolean useSpecial,
			boolean avoidAmbiguous, int minDigits, int minSpecial) {

		String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
		String DIGITS = "0123456789";
		String AMBIGUOUS = "B8G6I1l0OQDS5Z2,.;:|";
		String SPECIAL = "!@#$%^&*";
		// String SPECIAL = "!\"#$%&' ()*+, -. / :;<= >?@[\\]_{|}~";

		StringBuilder senha = new StringBuilder();

		int digitCount = 0, specialCount = 0;

		while (senha.length() < length) {

			// Escolha um conjunto de caracteres com base nas opções fornecidas
			String caracteresPermitidos = "";

			if (useUppercase)
				caracteresPermitidos += UPPERCASE;
			if (useLowercase)
				caracteresPermitidos += LOWERCASE;
			if (useDigits)
				caracteresPermitidos += DIGITS;
			if (useSpecial)
				caracteresPermitidos += SPECIAL;

			// Remova caracteres ambíguos se necessário
			if (avoidAmbiguous)
				caracteresPermitidos = caracteresPermitidos.replaceAll("[" + AMBIGUOUS + "]", "");

			// Escolha um caractere aleatório do conjunto
			char randomChar = caracteresPermitidos.charAt(secureRandom.nextInt(caracteresPermitidos.length()));

			// Adicione o caractere à senha
			senha.append(randomChar);

			// Atualize contadores de tipos de caracteres
			if (useDigits && DIGITS.indexOf(randomChar) != -1)
				digitCount++;
			if (useSpecial && SPECIAL.indexOf(randomChar) != -1)
				specialCount++;
		}

		// Garanta que a senha atenda aos requisitos mínimos
		// Se necessário, gere uma nova senha
		while (digitCount < minDigits || specialCount < minSpecial)
			return gerar(length, useUppercase, useLowercase, useDigits, useSpecial, avoidAmbiguous, minDigits,
					minSpecial);

		return senha.toString();
	}

	/*
	 * public static void main(String[] args) { String senha = gerarSenha(12, true, true,
	 * true, true, false, 2, 2); System.out.println("Generated Password: " + senha); }
	 */

}
