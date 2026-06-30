package br.ifsp.ctrltcc.comm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Consulta o monolito para verificar se um userId é membro de um projectId.
 *
 * O monolito deve expor GET /internal/projects/{projectId}/members/{userId}
 * retornando 200 se membro, 404 se nao.
 *
 */
@Component
public class MembershipClient {

    private static final Logger log = LoggerFactory.getLogger(MembershipClient.class);

    private final RestTemplate restTemplate;
    private final String monolitoBaseUrl;

    public MembershipClient(
            RestTemplate restTemplate,
            @Value("${monolito.base-url}") String monolitoBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.monolitoBaseUrl = monolitoBaseUrl;
    }

    public boolean isMember(Long projectId, Long userId) {
        String url = monolitoBaseUrl + "/internal/projects/" + projectId + "/members/" + userId;
        try {
            restTemplate.getForObject(url, Void.class);
            return true;
        } catch (Exception e) {
            log.debug("Membership check failed for project={} user={}: {}", projectId, userId, e.getMessage());
            return false;
        }
    }
}
