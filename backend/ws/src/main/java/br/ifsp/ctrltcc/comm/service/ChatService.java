package br.ifsp.ctrltcc.comm.service;

import br.ifsp.ctrltcc.comm.dto.CommDTO.ChatResponse;
import br.ifsp.ctrltcc.comm.exception.AccessDeniedException;
import br.ifsp.ctrltcc.comm.exception.ResourceNotFoundException;
import br.ifsp.ctrltcc.comm.mapper.CommMapper;
import br.ifsp.ctrltcc.comm.model.Chat;
import br.ifsp.ctrltcc.comm.repository.ChatRepository;
import br.ifsp.ctrltcc.comm.security.CallerContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final MembershipClient membershipClient;
    private final CommMapper commMapper;

    public ChatService(
            ChatRepository chatRepository,
            MembershipClient membershipClient,
            CommMapper commMapper
    ) {
        this.chatRepository = chatRepository;
        this.membershipClient = membershipClient;
        this.commMapper = commMapper;
    }

    /**
     * Chamado pelo monolito via POST /internal/chats ao criar um Project.
     * Idempotente: se o chat ja existe para esse projectId, retorna o existente.
     */
    public ChatResponse createForProject(Long projectId) {
        return chatRepository.findByProjectId(projectId)
                .map(commMapper::toChatResponse)
                .orElseGet(() -> {
                    Chat chat = chatRepository.save(new Chat(projectId));
                    return commMapper.toChatResponse(chat);
                });
    }

    @Transactional(readOnly = true)
    public ChatResponse findByProject(Long projectId, CallerContext caller) {
        assertMember(projectId, caller);
        Chat chat = chatRepository.findByProjectId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chat nao encontrado para o projeto: " + projectId));
        return commMapper.toChatResponse(chat);
    }

    @Transactional(readOnly = true)
    public ChatResponse findById(Long chatId, CallerContext caller) {
        Chat chat = getChatOrThrow(chatId);
        assertMember(chat.getProjectId(), caller);
        return commMapper.toChatResponse(chat);
    }

    public Chat getChatOrThrow(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat nao encontrado: " + chatId));
    }

    public void assertMember(Long projectId, CallerContext caller) {
        if (!membershipClient.isMember(projectId, caller.userId())) {
            throw new AccessDeniedException("Voce nao e membro deste projeto.");
        }
    }
}
