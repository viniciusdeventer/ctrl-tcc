package br.ifsp.ctrltcc.dto.user;

import br.ifsp.ctrltcc.model.Role;
import jakarta.validation.constraints.Email;

public record UpdateUserRequest(

        @Email(message = "E-mail inválido")
        String email,

        String name,

        Role role

) {}
