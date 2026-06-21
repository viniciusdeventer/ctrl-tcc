package br.ifsp.ctrltcc.service;

import br.ifsp.ctrltcc.dto.project.ProjectDTO.ProjectResponse;
import br.ifsp.ctrltcc.exception.AccessDeniedException;
import br.ifsp.ctrltcc.exception.ResourceNotFoundException;
import br.ifsp.ctrltcc.mapper.ProjectMapper;
import br.ifsp.ctrltcc.model.Chat;
import br.ifsp.ctrltcc.model.Project;
import br.ifsp.ctrltcc.model.Proposal;
import br.ifsp.ctrltcc.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProposalService proposalService;
    private final ProjectMemberService projectMemberService;
    private final ProjectMapper projectMapper;

    public ProjectService(
            ProjectRepository projectRepository,
            ProposalService proposalService,
            ProjectMemberService projectMemberService,
            ProjectMapper projectMapper
    ) {
        this.projectRepository = projectRepository;
        this.proposalService = proposalService;
        this.projectMemberService = projectMemberService;
        this.projectMapper = projectMapper;
    }

    public ProjectResponse acceptProposalAndCreateProject(Long proposalId, Long advisorId) {
        Proposal proposal = proposalService.findById(proposalId);

        if (!proposal.getAdvisor().getId().equals(advisorId)) {
            throw new AccessDeniedException("Only the proposal's advisor can accept it.");
        }

        proposal.accept();

        Project project = new Project(proposal);
        project.attachChat(new Chat(project));

        project = projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(Long projectId) {
        return projectMapper.toResponse(findById(projectId));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> listByMember(Long userId) {
        return projectRepository.findAllByMemberUserId(userId).stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    public ProjectResponse finish(Long projectId, Long requesterId) {
        Project project = findById(projectId);
        ensureIsMember(project, requesterId);
        project.finish();
        return projectMapper.toResponse(project);
    }

    public ProjectResponse cancel(Long projectId, Long requesterId) {
        Project project = findById(projectId);
        ensureIsMember(project, requesterId);
        project.cancel();
        return projectMapper.toResponse(project);
    }

    public Project getProjectOrThrow(Long projectId) {
        return findById(projectId);
    }

    private Project findById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
    }

    private void ensureIsMember(Project project, Long userId) {
        if (!projectMemberService.isMember(project.getId(), userId)) {
            throw new AccessDeniedException("Only project members can perform this action.");
        }
    }
}
