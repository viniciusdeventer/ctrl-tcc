package br.ifsp.ctrltcc.controller;

import br.ifsp.ctrltcc.dto.proposal.ProposalDTO.CreateProposalRequest;
import br.ifsp.ctrltcc.dto.proposal.ProposalDTO.ProposalFeedbackRequest;
import br.ifsp.ctrltcc.dto.proposal.ProposalDTO.ProposalResponse;
import br.ifsp.ctrltcc.dto.proposal.ProposalDTO.ResubmitProposalRequest;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.security.AuthenticatedUserResolver;
import br.ifsp.ctrltcc.service.ProposalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/proposals")
public class ProposalController {

    private final ProposalService proposalService;
    private final AuthenticatedUserResolver userResolver;

    public ProposalController(ProposalService proposalService, AuthenticatedUserResolver userResolver) {
        this.proposalService = proposalService;
        this.userResolver = userResolver;
    }

    @PostMapping
    public ResponseEntity<ProposalResponse> create(@Valid @RequestBody CreateProposalRequest request) {
        User student = userResolver.current();
        ProposalResponse response = proposalService.create(student.getId(), request);
        return ResponseEntity.created(URI.create("/api/proposals/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProposalResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(proposalService.getById(id));
    }

    // Lista as propostas do usuario autenticado. O papel (ADVISOR/TEACHER) decide
    // se a consulta e "minhas propostas enviadas" ou "propostas recebidas para avaliar".
    @GetMapping("/mine")
    public ResponseEntity<List<ProposalResponse>> listMine() {
        User user = userResolver.current();
        List<ProposalResponse> proposals = switch (user.getRole()) {
            case STUDENT -> proposalService.listByStudent(user.getId());
            case ADVISOR -> proposalService.listByAdvisor(user.getId());
            case ADMIN -> List.of();
        };
        return ResponseEntity.ok(proposals);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ProposalResponse> reject(
            @PathVariable Long id,
            @Valid @RequestBody ProposalFeedbackRequest request
    ) {
        User advisor = userResolver.current();
        return ResponseEntity.ok(proposalService.reject(id, advisor.getId(), request));
    }

    @PostMapping("/{id}/request-changes")
    public ResponseEntity<ProposalResponse> requestChanges(
            @PathVariable Long id,
            @Valid @RequestBody ProposalFeedbackRequest request
    ) {
        User advisor = userResolver.current();
        return ResponseEntity.ok(proposalService.requestChanges(id, advisor.getId(), request));
    }

    @PostMapping("/{id}/resubmit")
    public ResponseEntity<ProposalResponse> resubmit(
            @PathVariable Long id,
            @Valid @RequestBody ResubmitProposalRequest request
    ) {
        User student = userResolver.current();
        return ResponseEntity.ok(proposalService.resubmit(id, student.getId(), request));
    }

    // O "accept" que efetivamente vira Project esta no ProjectController,
    // ja que o resultado dessa acao e a criacao de um Project (+ Chat).
    // Ver ProjectController#acceptProposal.
}