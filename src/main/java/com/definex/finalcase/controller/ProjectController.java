package com.definex.finalcase.controller;

import com.definex.finalcase.domain.request.ProjectRequest;
import com.definex.finalcase.domain.response.ProjectResponse;
import com.definex.finalcase.domain.response.UserResponse;
import com.definex.finalcase.security.permission.AnyTeamRole;
import com.definex.finalcase.security.permission.ProjectManagerRole;
import com.definex.finalcase.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @ProjectManagerRole
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse response = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AnyTeamRole
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable UUID projectId) {
        ProjectResponse response = projectService.getProjectById(projectId);
        return ResponseEntity.ok(response);
    }

    @AnyTeamRole
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> response = projectService.getAllProjects();
        return ResponseEntity.ok(response);
    }

    @ProjectManagerRole
    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectRequest request) {
        ProjectResponse response = projectService.updateProject(projectId, request);
        return ResponseEntity.ok(response);
    }

    @ProjectManagerRole
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    @ProjectManagerRole
    @PatchMapping("/{projectId}/assign/{userId}")
    public ResponseEntity<ProjectResponse> assignUserToProject(
            @PathVariable UUID projectId, @PathVariable UUID userId) {
        ProjectResponse response = projectService.assignUserToProject(projectId, userId);
        return ResponseEntity.ok(response);
    }

    @ProjectManagerRole
    @PatchMapping("/{projectId}/remove/{userId}")
    public ResponseEntity<ProjectResponse> removeUserFromProject(
            @PathVariable UUID projectId, @PathVariable UUID userId) {
        ProjectResponse response = projectService.removeUserFromProject(projectId, userId);
        return ResponseEntity.ok(response);
    }

    @AnyTeamRole
    @GetMapping("/{projectId}/team")
    public ResponseEntity<List<UserResponse>> getProjectTeam(@PathVariable UUID projectId) {
        List<UserResponse> response = projectService.getProjectTeam(projectId);
        return ResponseEntity.ok(response);
    }
}
