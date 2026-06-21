package br.ifsp.ctrltcc.service;

import br.ifsp.ctrltcc.exception.AccessDeniedException;
import br.ifsp.ctrltcc.model.Project;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.repository.ProjectMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

    public ProjectMemberService(ProjectMemberRepository projectMemberRepository) {
        this.projectMemberRepository = projectMemberRepository;
    }

    public boolean isMember(Project project, User user) {
        return isMember(project.getId(), user.getId());
    }

    public boolean isMember(Long projectId, Long userId) {
        return projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
    }

    public void assertMember(Project project, User user) {
        if (!isMember(project, user)) {
            throw new AccessDeniedException("Você não participa deste projeto.");
        }
    }
}
