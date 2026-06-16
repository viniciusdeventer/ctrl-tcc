package br.ifsp.ctrltcc.service;

import br.ifsp.ctrltcc.dto.user.ChangePasswordRequest;
import br.ifsp.ctrltcc.dto.user.CreateUserRequest;
import br.ifsp.ctrltcc.dto.user.UpdateUserRequest;
import br.ifsp.ctrltcc.dto.user.UserResponse;
import br.ifsp.ctrltcc.exception.EmailAlreadyExistsException;
import br.ifsp.ctrltcc.exception.ResourceNotFoundException;
import br.ifsp.ctrltcc.mapper.UserMapper;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    public UserResponse create(CreateUserRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new EmailAlreadyExistsException(req.email());
        }
        User user = userMapper.toEntity(req);
        return userMapper.toResponse(userRepository.save(user));
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return userMapper.toResponse(getOrThrow(id));
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public UserResponse update(Long id, UpdateUserRequest req) {
        User user = getOrThrow(id);

        if (req.email() != null && !req.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(req.email())) {
                throw new EmailAlreadyExistsException(req.email());
            }
            user.setEmail(req.email());
        }
        if (req.name() != null) user.setName(req.name());
        if (req.role() != null) user.setRole(req.role());

        return userMapper.toResponse(userRepository.save(user));
    }

    public void changePassword(Long id, ChangePasswordRequest req) {
        User user = getOrThrow(id);

        if (!passwordEncoder.matches(req.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }
        user.setPassword(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + id);
        }
        userRepository.deleteById(id);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private User getOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }
}
