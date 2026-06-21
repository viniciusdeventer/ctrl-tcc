package br.ifsp.ctrltcc.dto.project;

import br.ifsp.ctrltcc.model.ProjectRole;
import br.ifsp.ctrltcc.model.ProjectStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectDTO {

	public record ProjectResponse(
	        Long id,
	        String title,
	        String description,
	        Long proposalId,
	        ProjectStatus status,
	        LocalDateTime createdAt,
	        List<ProjectMemberResponse> members,
	        Long chatId
	) {
	}
	
	public record ProjectMemberResponse(
	        Long userId,
	        String userName,
	        String userEmail,
	        ProjectRole role,
	        LocalDateTime joinedAt
	) {
	}
	
	private ProjectDTO() {
    }

}
