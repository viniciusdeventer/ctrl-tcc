package br.ifsp.ctrltcc.security;

import br.ifsp.ctrltcc.exception.ResourceNotFoundException;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserResolver {

    private final UserRepository userRepository;

    public AuthenticatedUserResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Retorna o User autenticado a partir do SecurityContext (REST) ou de um Authentication (WebSocket). */
    public User resolve(Authentication authentication) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado: " + email));
    }

    /** Atalho para contextos REST onde o SecurityContextHolder já está populado. */
    public User current() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return resolve(auth);
    }
}
