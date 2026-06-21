package br.ifsp.ctrltcc.repository;

import br.ifsp.ctrltcc.model.Chat;
import br.ifsp.ctrltcc.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByChatOrderBySentAtDesc(Chat chat, Pageable pageable);
}
