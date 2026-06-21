package br.ifsp.ctrltcc.dto.proposal;

import java.time.LocalDateTime;

import br.ifsp.ctrltcc.model.ProposalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProposalDTO {
	
	public record CreateProposalRequest(
	        @NotBlank(message = "Title is required")
	        @Size(max = 255, message = "Title must be at most 255 characters")
	        String title,

	        @NotBlank(message = "Description is required")
	        @Size(max = 5000, message = "Description must be at most 5000 characters")
	        String description,

	        @NotNull(message = "Advisor is required")
	        Long advisorId
	) {
	}
	
	public record ProposalFeedbackRequest(
	        @NotBlank(message = "Feedback is required")
	        @Size(max = 2000, message = "Feedback must be at most 2000 characters")
	        String feedback
	) {
	}
	
	public record ResubmitProposalRequest(
	        @NotBlank(message = "Title is required")
	        @Size(max = 255, message = "Title must be at most 255 characters")
	        String title,

	        @NotBlank(message = "Description is required")
	        @Size(max = 5000, message = "Description must be at most 5000 characters")
	        String description
	) {
	}
	
	public record ProposalResponse(
	        Long id,
	        String title,
	        String description,
	        Long studentId,
	        String studentName,
	        Long advisorId,
	        String advisorName,
	        ProposalStatus status,
	        String advisorFeedback,
	        LocalDateTime createdAt,
	        LocalDateTime answeredAt
	) {
	}
	
}
