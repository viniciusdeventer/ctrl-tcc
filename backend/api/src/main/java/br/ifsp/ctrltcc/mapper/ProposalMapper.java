package br.ifsp.ctrltcc.mapper;

import br.ifsp.ctrltcc.dto.proposal.ProposalDTO.ProposalResponse;
import br.ifsp.ctrltcc.model.Proposal;
import org.springframework.stereotype.Component;

@Component
public class ProposalMapper {

    public ProposalResponse toResponse(Proposal proposal) {
        return new ProposalResponse(
                proposal.getId(),
                proposal.getTitle(),
                proposal.getDescription(),
                proposal.getStudent().getId(),
                proposal.getStudent().getName(),
                proposal.getAdvisor().getId(),
                proposal.getAdvisor().getName(),
                proposal.getStatus(),
                proposal.getAdvisorFeedback(),
                proposal.getCreatedAt(),
                proposal.getAnsweredAt()
        );
    }
}
