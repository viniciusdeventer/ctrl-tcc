package br.ifsp.ctrltcc.service;

import br.ifsp.ctrltcc.dto.chat.ChatRoomDTOs.AddMemberRequest;
import br.ifsp.ctrltcc.dto.chat.ChatRoomDTOs.ChatRoomResponse;
import br.ifsp.ctrltcc.dto.chat.ChatRoomDTOs.CreateRoomRequest;
import br.ifsp.ctrltcc.exception.ResourceNotFoundException;
import br.ifsp.ctrltcc.mapper.ChatMapper;
import br.ifsp.ctrltcc.model.ChatRoom;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.repository.ChatRoomRepository;
import br.ifsp.ctrltcc.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    public ChatRoomService(ChatRoomRepository roomRepository,
                           UserRepository userRepository,
                           ChatMapper chatMapper) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.chatMapper = chatMapper;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    public ChatRoomResponse create(CreateRoomRequest req, User creator) {
        ChatRoom room = new ChatRoom(req.name(), req.description(), creator);

        Set<User> extraMembers = resolveUsers(req.memberIds());
        extraMembers.forEach(room::addMember);

        return chatMapper.toRoomResponse(roomRepository.save(room));
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> findMyRooms(User user) {
        return roomRepository.findAllByMember(user).stream()
                .map(chatMapper::toRoomResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChatRoomResponse findById(Long roomId, User requester) {
        ChatRoom room = getRoomOrThrow(roomId);
        assertMember(room, requester);
        return chatMapper.toRoomResponse(room);
    }

    // ── Members ───────────────────────────────────────────────────────────────

    public ChatRoomResponse addMembers(Long roomId, AddMemberRequest req, User requester) {
        ChatRoom room = getRoomOrThrow(roomId);
        assertMember(room, requester);

        resolveUsers(req.userIds()).forEach(room::addMember);

        return chatMapper.toRoomResponse(roomRepository.save(room));
    }

    public ChatRoomResponse removeMember(Long roomId, Long userId, User requester) {
        ChatRoom room = getRoomOrThrow(roomId);
        assertMember(room, requester);

        User target = getUserOrThrow(userId);

        if (target.equals(room.getCreatedBy())) {
            throw new IllegalArgumentException("Não é possível remover o criador da sala");
        }

        room.removeMember(target);
        return chatMapper.toRoomResponse(roomRepository.save(room));
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    public ChatRoom getRoomOrThrow(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada: " + roomId));
    }

    public void assertMember(ChatRoom room, User user) {
        if (!room.hasMember(user)) {
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
