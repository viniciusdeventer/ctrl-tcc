package br.ifsp.ctrltcc.repository;

import br.ifsp.ctrltcc.model.ChatRoom;
import br.ifsp.ctrltcc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT r FROM ChatRoom r JOIN r.members m WHERE m = :user ORDER BY r.createdAt DESC")
    List<ChatRoom> findAllByMember(@Param("user") User user);
}
