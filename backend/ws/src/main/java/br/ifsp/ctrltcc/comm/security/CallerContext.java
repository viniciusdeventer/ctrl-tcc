package br.ifsp.ctrltcc.comm.security;

/**
 * Contexto do chamador autenticado, extraido do JWT.
 * Substitui a entidade User do monolito — o microsservico nao tem acesso
 * ao banco de usuarios, apenas ao token validado.
 */
public record CallerContext(Long userId, String email, String name) {
}
