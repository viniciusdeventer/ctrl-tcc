package br.ifsp.ctrltcc.dto.auth;

public record LoginResponse(

        String token,
        String type,
        Long id,
        String name,
        String email,
        String role

) {

	public LoginResponse(String token, Long id, String name, String email, String role) {
        this(token, "Bearer", id, name, email, role);
    }
}
