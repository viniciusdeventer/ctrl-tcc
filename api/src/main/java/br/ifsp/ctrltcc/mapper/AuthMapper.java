package br.ifsp.ctrltcc.mapper;

import br.ifsp.ctrltcc.dto.auth.LoginResponse;
import br.ifsp.ctrltcc.model.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    /** Monta o LoginResponse a partir do token JWT e da entidade User. */
    public LoginResponse toLoginResponse(String token, User user) {
        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
