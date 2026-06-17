package br.ifsp.ctrltcc.repository;

import br.ifsp.ctrltcc.model.Chat;
import br.ifsp.ctrltcc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c JOIN c.members m WHERE m = :user ORDER BY c.createdAt DESC")
    List<Chat> findAllByMember(@Param("user") User user);
}
