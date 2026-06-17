package br.ifsp.ctrltcc.dto.user;

import br.ifsp.ctrltcc.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDTO {
	public record CreateUserRequest(

	        @NotBlank(message = "E-mail é obrigatório")
	        @Email(message = "E-mail inválido")
	        String email,

	        @NotBlank(message = "Senha é obrigatória")
	        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
	        String password,

	        @NotBlank(message = "Nome é obrigatório")
	        String name,

	        @NotNull(message = "Tipo de acesso é obrigatório")
	        Role role

	) {}
	
	public record UpdateUserRequest(

	        @Email(message = "E-mail inválido")
	        String email,

	        String name,

	        Role role

	) {}
	
	public record ChangePasswordRequest(

	        @NotBlank(message = "Senha atual é obrigatória")
	        String currentPassword,

	        @NotBlank(message = "Nova senha é obrigatória")
	        @Size(min = 6, message = "Nova senha deve ter no mínimo 6 caracteres")
	        String newPassword

	) {}
	
	public record UserResponse(

	        Long id,
	        String email,
	        String name,
	        String role

	) {}
}