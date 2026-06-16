package br.ifsp.ctrltcc.mapper;

import br.ifsp.ctrltcc.dto.user.CreateUserRequest;
import br.ifsp.ctrltcc.dto.user.UserResponse;
import br.ifsp.ctrltcc.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /** CreateUserRequest → nova entidade User (senha já encodada). */
    public User toEntity(CreateUserRequest req) {
        return new User(
                req.email(),
                passwordEncoder.encode(req.password()),
                req.name(),
                req.role()
        );
    }

    /** User → UserResponse (sem dados sensíveis). */
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
    }
}
