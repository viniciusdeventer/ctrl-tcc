package br.ifsp.ctrltcc.comm.repository;

import br.ifsp.ctrltcc.comm.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByProjectId(Long projectId);

    boolean existsByProjectId(Long projectId);
}
