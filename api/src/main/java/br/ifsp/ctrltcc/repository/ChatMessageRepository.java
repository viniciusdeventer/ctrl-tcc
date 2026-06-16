package br.ifsp.ctrltcc.repository;

import br.ifsp.ctrltcc.model.ChatMessage;
import br.ifsp.ctrltcc.model.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByRoomOrderBySentAtDesc(ChatRoom room, Pageable pageable);
}
