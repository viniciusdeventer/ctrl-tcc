package br.ifsp.ctrltcc.dto.user;

public record UserResponse(

        Long id,
        String email,
        String name,
        String role

) {}
