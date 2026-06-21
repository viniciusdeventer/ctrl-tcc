package br.ifsp.ctrltcc.repository;

import br.ifsp.ctrltcc.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByProjectId(Long projectId);
}
