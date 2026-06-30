package br.ifsp.ctrltcc.comm.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CallerResolver {

    public CallerContext current() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CallerContext caller)) {
            throw new IllegalStateException("No authenticated caller in context.");
        }
        return caller;
    }

    public CallerContext resolve(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof CallerContext caller)) {
            throw new IllegalStateException("No authenticated caller in context.");
        }
        return caller;
    }
}
