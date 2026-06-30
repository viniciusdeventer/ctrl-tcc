package br.ifsp.ctrltcc.service;

import br.ifsp.ctrltcc.dto.auth.AuthDTO.LoginRequest;
import br.ifsp.ctrltcc.dto.auth.AuthDTO.LoginResponse;
import br.ifsp.ctrltcc.mapper.AuthMapper;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.repository.UserRepository;
import br.ifsp.ctrltcc.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthMapper authMapper;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtUtil jwtUtil,
                       AuthMapper authMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authMapper = authMapper;
    }

    public LoginResponse login(LoginRequest req) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.email(),
                        req.password()
                )
        );

        User user = userRepository.findByEmail(req.email()).orElseThrow();

        String token = jwtUtil.generateToken(user);

        return authMapper.toLoginResponse(token, user);
    }
}