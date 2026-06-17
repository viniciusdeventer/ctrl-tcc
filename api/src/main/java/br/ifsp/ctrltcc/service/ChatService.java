package br.ifsp.ctrltcc.service;

import br.ifsp.ctrltcc.dto.chat.ChatDTO.AddMemberRequest;
import br.ifsp.ctrltcc.dto.chat.ChatDTO.ChatResponse;
import br.ifsp.ctrltcc.dto.chat.ChatDTO.CreateChatRequest;
import br.ifsp.ctrltcc.exception.ResourceNotFoundException;
import br.ifsp.ctrltcc.mapper.ChatMapper;
import br.ifsp.ctrltcc.model.Chat;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.repository.ChatRepository;
import br.ifsp.ctrltcc.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    public ChatService(ChatRepository chatRepository,
                           UserRepository userRepository,
                           ChatMapper chatMapper) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.chatMapper = chatMapper;
    }

    public ChatResponse create(CreateChatRequest req, User creator) {
        Chat chat = new Chat(req.name(), req.description(), creator);

        Set<User> extraMembers = resolveUsers(req.memberIds());
        extraMembers.forEach(chat::addMember);

        return chatMapper.toChatResponse(chatRepository.save(chat));
    }

    @Transactional(readOnly = true)
    public List<ChatResponse> findMyChats(User user) {
        return chatRepository.findAllByMember(user).stream()
                .map(chatMapper::toChatResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChatResponse findById(Long chatId, User requester) {
        Chat chat = getChatOrThrow(chatId);
        assertMember(chat, requester);
        return chatMapper.toChatResponse(chat);
    }

    public ChatResponse addMembers(Long chatId, AddMemberRequest req, User requester) {
        Chat chat = getChatOrThrow(chatId);
        assertMember(chat, requester);

        resolveUsers(req.userIds()).forEach(chat::addMember);

        return chatMapper.toChatResponse(chatRepository.save(chat));
    }

    public ChatResponse removeMember(Long chatId, Long userId, User requester) {
        Chat chat = getChatOrThrow(chatId);
        assertMember(chat, requester);

        User target = getUserOrThrow(userId);

        if (target.equals(chat.getCreatedBy())) {
            throw new IllegalArgumentException("Não é possível remover o criador da sala");
        }

        chat.removeMember(target);
        return chatMapper.toChatResponse(chatRepository.save(chat));
    }

    public Chat getChatOrThrow(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada: " + chatId));
    }

    public void assertMember(Chat chat, User user) {
        if (!chat.hasMember(user)) {
            throw new AccessDeniedException("Você não é membro desta sala");
        }
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + userId));
    }

    private Set<User> resolveUsers(Set<Long> ids) {
        return ids.stream()
                .map(this::getUserOrThrow)
                .collect(Collectors.toSet());
    }
}
