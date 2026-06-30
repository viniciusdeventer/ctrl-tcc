package br.ifsp.ctrltcc.controller;

import br.ifsp.ctrltcc.exception.ResourceNotFoundException;
import br.ifsp.ctrltcc.repository.ProjectMemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints internos consumidos por outros servicos (ex: microsservico de Communication).
 * Em producao, proteger por IP allowlist ou API key — nao deve ser acessivel publicamente.
 */
@RestController
@RequestMapping("/internal")
public class InternalProjectController {

    private final ProjectMemberRepository projectMemberRepository;

    public InternalProjectController(ProjectMemberRepository projectMemberRepository) {
        this.projectMemberRepository = projectMemberRepository;
    }

    @GetMapping("/projects/{projectId}/members/{userId}")
    public ResponseEntity<Void> checkMembership(
            @PathVariable Long projectId,
            @PathVariable Long userId
    ) {
        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new ResourceNotFoundException("User is not a member of this project.");
        }
        return ResponseEntity.ok().build();
    }
}
