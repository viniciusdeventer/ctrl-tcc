package br.ifsp.ctrltcc.service;

import br.ifsp.ctrltcc.dto.proposal.ProposalDTO.CreateProposalRequest;
import br.ifsp.ctrltcc.dto.proposal.ProposalDTO.ProposalFeedbackRequest;
import br.ifsp.ctrltcc.dto.proposal.ProposalDTO.ProposalResponse;
import br.ifsp.ctrltcc.dto.proposal.ProposalDTO.ResubmitProposalRequest;
import br.ifsp.ctrltcc.exception.AccessDeniedException;
import br.ifsp.ctrltcc.exception.ResourceNotFoundException;
import br.ifsp.ctrltcc.mapper.ProposalMapper;
import br.ifsp.ctrltcc.model.Proposal;
import br.ifsp.ctrltcc.model.ProposalStatus;
import br.ifsp.ctrltcc.model.Role;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.repository.ProposalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final UserService userService;
    private final ProposalMapper proposalMapper;

    public ProposalService(
            ProposalRepository proposalRepository,
            UserService userService,
            ProposalMapper proposalMapper
    ) {
        this.proposalRepository = proposalRepository;
        this.userService = userService;
        this.proposalMapper = proposalMapper;
    }

    public ProposalResponse create(Long studentId, CreateProposalRequest request) {
        User student = userService.getByIdAndRole(studentId, Role.STUDENT);
        User advisor = userService.getByIdAndRole(request.advisorId(), Role.ADVISOR);

        boolean alreadyPending = proposalRepository.existsByStudentIdAndAdvisorIdAndStatus(
                studentId, advisor.getId(), ProposalStatus.PENDING
        );
        if (alreadyPending) {
            throw new IllegalStateException("There is already a pending proposal for this advisor.");
        }

        Proposal proposal = new Proposal(request.title(), request.description(), student, advisor);
        proposal = proposalRepository.save(proposal);
        return proposalMapper.toResponse(proposal);
    }

    @Transactional(readOnly = true)
    public ProposalResponse getById(Long proposalId) {
        return proposalMapper.toResponse(findById(proposalId));
    }

    @Transactional(readOnly = true)
    public List<ProposalResponse> listByStudent(Long studentId) {
        return proposalRepository.findByStudentId(studentId).stream()
                .map(proposalMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProposalResponse> listByAdvisor(Long advisorId) {
        return proposalRepository.findByAdvisorId(advisorId).stream()
                .map(proposalMapper::toResponse)
                .toList();
    }

    public ProposalResponse accept(Long proposalId, Long advisorId) {
        Proposal proposal = findById(proposalId);
        ensureIsAdvisor(proposal, advisorId);
        proposal.accept();
        return proposalMapper.toResponse(proposal);
    }

    public ProposalResponse reject(Long proposalId, Long advisorId, ProposalFeedbackRequest request) {
        Proposal proposal = findById(proposalId);
        ensureIsAdvisor(proposal, advisorId);
        proposal.reject(request.feedback());
        return proposalMapper.toResponse(proposal);
    }

    public ProposalResponse requestChanges(Long proposalId, Long advisorId, ProposalFeedbackRequest request) {
        Proposal proposal = findById(proposalId);
        ensureIsAdvisor(proposal, advisorId);
        proposal.requestChanges(request.feedback());
        return proposalMapper.toResponse(proposal);
    }

    public ProposalResponse resubmit(Long proposalId, Long studentId, ResubmitProposalRequest request) {
        Proposal proposal = findById(proposalId);
        ensureIsStudent(proposal, studentId);
        proposal.resubmit(request.title(), request.description());
        return proposalMapper.toResponse(proposal);
    }

    Proposal findById(Long proposalId) {
        return proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("Proposal not found: " + proposalId));
    }

    private void ensureIsAdvisor(Proposal proposal, Long advisorId) {
        if (!proposal.getAdvisor().getId().equals(advisorId)) {
            throw new AccessDeniedException("Only the proposal's advisor can perform this action.");
        }
    }

    private void ensureIsStudent(Proposal proposal, Long studentId) {
        if (!proposal.getStudent().getId().equals(studentId)) {
            throw new AccessDeniedException("Only the proposal's student can perform this action.");
        }
    }
}
