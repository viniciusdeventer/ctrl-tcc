package br.ifsp.ctrltcc.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDTO {
	public record LoginRequest(

	        @NotBlank(message = "E-mail é obrigatório")
	        @Email(message = "E-mail inválido")
	        String email,

	        @NotBlank(message = "Senha é obrigatória")
	        String password

	) {}
	
	public record LoginResponse(

	        String token,
	        String type,
	        Long id,
	        String name,
	        String email,
	        String role

	) {

		public LoginResponse(String token, Long id, String name, String email, String role) {
	        this(token, "Bearer", id, name, email, role);
	    }
	}
}