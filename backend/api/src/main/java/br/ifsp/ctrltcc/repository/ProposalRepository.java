package br.ifsp.ctrltcc.repository;

import br.ifsp.ctrltcc.model.Proposal;
import br.ifsp.ctrltcc.model.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    List<Proposal> findByAdvisorIdAndStatus(Long advisorId, ProposalStatus status);

    List<Proposal> findByStudentId(Long studentId);

    List<Proposal> findByAdvisorId(Long advisorId);

    boolean existsByStudentIdAndAdvisorIdAndStatus(Long studentId, Long advisorId, ProposalStatus status);
}
