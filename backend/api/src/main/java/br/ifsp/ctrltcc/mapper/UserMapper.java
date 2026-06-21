package br.ifsp.ctrltcc.mapper;

import br.ifsp.ctrltcc.dto.user.UserDTO.CreateUserRequest;
import br.ifsp.ctrltcc.dto.user.UserDTO.UserResponse;
import br.ifsp.ctrltcc.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toEntity(CreateUserRequest req) {
        return new User(
                req.email(),
                passwordEncoder.encode(req.password()),
                req.name(),
                req.role()
        );
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
    }
}
