package br.ifsp.ctrltcc.controller;

import br.ifsp.ctrltcc.dto.project.ProjectDTO.ProjectResponse;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.security.AuthenticatedUserResolver;
import br.ifsp.ctrltcc.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final AuthenticatedUserResolver userResolver;

    public ProjectController(ProjectService projectService, AuthenticatedUserResolver userResolver) {
        this.projectService = projectService;
        this.userResolver = userResolver;
    }

    @PostMapping("/proposal/{proposalId}")
    public ResponseEntity<ProjectResponse> acceptProposal(@PathVariable Long proposalId) {
        User advisor = userResolver.current();
        ProjectResponse response = projectService.acceptProposalAndCreateProject(proposalId, advisor.getId());
        return ResponseEntity.created(URI.create("/api/projects/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> listMine() {
        User user = userResolver.current();
        return ResponseEntity.ok(projectService.listByMember(user.getId()));
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<ProjectResponse> finish(@PathVariable Long id) {
        User requester = userResolver.current();
        return ResponseEntity.ok(projectService.finish(id, requester.getId()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ProjectResponse> cancel(@PathVariable Long id) {
        User requester = userResolver.current();
        return ResponseEntity.ok(projectService.cancel(id, requester.getId()));
    }
}