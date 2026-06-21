package br.ifsp.ctrltcc.service;

import br.ifsp.ctrltcc.dto.chat.ChatDTO.ChatResponse;
import br.ifsp.ctrltcc.exception.AccessDeniedException;
import br.ifsp.ctrltcc.exception.ResourceNotFoundException;
import br.ifsp.ctrltcc.mapper.ChatMapper;
import br.ifsp.ctrltcc.model.Chat;
import br.ifsp.ctrltcc.model.Project;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.repository.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Chat agora nasce automaticamente junto com o Project (ver ProjectService),
 * e e 1:1 com ele. Nao ha criacao isolada de chat nem gestao de membros propria:
 * "quem pode acessar o chat" e exatamente "quem e ProjectMember do projeto".
 */
@Service
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;
    private final ChatMapper chatMapper;

    public ChatService(
            ChatRepository chatRepository,
            ProjectService projectService,
            ProjectMemberService projectMemberService,
            ChatMapper chatMapper
    ) {
        this.chatRepository = chatRepository;
        this.projectService = projectService;
        this.projectMemberService = projectMemberService;
        this.chatMapper = chatMapper;
    }

    @Transactional(readOnly = true)
    public ChatResponse findById(Long chatId, User requester) {
        Chat chat = getChatOrThrow(chatId);
        assertMember(chat, requester);
        return chatMapper.toChatResponse(chat);
    }

    @Transactional(readOnly = true)
    public ChatResponse findByProject(Long projectId, User requester) {
        Project project = projectService.getProjectOrThrow(projectId);
        projectMemberService.assertMember(project, requester);

        Chat chat = chatRepository.findByProjectId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chat não encontrado para o projeto: " + projectId
                ));
        return chatMapper.toChatResponse(chat);
    }

    public Chat getChatOrThrow(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chat não encontrado: " + chatId
                ));
    }

    public void assertMember(Chat chat, User user) {
        if (!projectMemberService.isMember(chat.getProject(), user)) {
            throw new AccessDeniedException("Você não participa deste projeto.");
        }
    }
}
