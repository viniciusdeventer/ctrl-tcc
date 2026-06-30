package br.ifsp.ctrltcc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Chama o microsservico ctrltcc-communication para criar o Chat
 * assim que um Project e criado no monolito.
 *
 * Endpoint alvo: POST {comm.base-url}/internal/chats
 */
@Component
public class ChatServiceClient {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceClient.class);

    private final RestTemplate restTemplate;
    private final String commBaseUrl;

    public ChatServiceClient(
            RestTemplate restTemplate,
            @Value("${communication.base-url}") String commBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.commBaseUrl = commBaseUrl;
    }

    /**
     * Cria o chat para o projeto no microsservico.
     * Retorna o chatId criado, ou null se o servico estiver indisponivel.
     *
     */
    public Long createChatForProject(Long projectId) {
        String url = commBaseUrl + "/internal/chats";
        try {
            Map<?, ?> response = restTemplate.postForObject(url, Map.of("projectId", projectId), Map.class);
            if (response != null && response.get("id") instanceof Number id) {
                return id.longValue();
            }
        } catch (Exception e) {
            log.error("Falha ao criar chat para o projeto {}: {}", projectId, e.getMessage());
        }
        return null;
    }
}
