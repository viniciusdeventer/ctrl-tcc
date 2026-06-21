package br.ifsp.ctrltcc.mapper;

import br.ifsp.ctrltcc.dto.project.ProjectDTO.ProjectMemberResponse;
import br.ifsp.ctrltcc.dto.project.ProjectDTO.ProjectResponse;
import br.ifsp.ctrltcc.model.Project;
import br.ifsp.ctrltcc.model.ProjectMember;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectMapper {

    public ProjectResponse toResponse(Project project) {
        List<ProjectMemberResponse> members = project.getMembers().stream()
                .map(this::toMemberResponse)
                .toList();

        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getProposal().getId(),
                project.getStatus(),
                project.getCreatedAt(),
                members,
                project.getChat() != null ? project.getChat().getId() : null
        );
    }

    public ProjectMemberResponse toMemberResponse(ProjectMember member) {
        return new ProjectMemberResponse(
                member.getUser().getId(),
                member.getUser().getName(),
                member.getUser().getEmail(),
                member.getRole(),
                member.getJoinedAt()
        );
    }
}
