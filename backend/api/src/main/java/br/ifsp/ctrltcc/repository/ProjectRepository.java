package br.ifsp.ctrltcc.repository;

import br.ifsp.ctrltcc.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByProposalId(Long proposalId);

    @Query("""
            SELECT p FROM Project p
            JOIN p.members m
            WHERE m.user.id = :userId
            """)
    List<Project> findAllByMemberUserId(@Param("userId") Long userId);

    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
            FROM Project p
            JOIN p.members m
            WHERE p.id = :projectId AND m.user.id = :userId
            """)
    boolean existsByIdAndMemberUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
