package com.definex.finalcase.service;

import com.definex.finalcase.domain.entity.Project;
import com.definex.finalcase.domain.entity.User;
import com.definex.finalcase.domain.request.ProjectRequest;
import com.definex.finalcase.domain.response.ProjectResponse;
import com.definex.finalcase.domain.response.UserResponse;
import com.definex.finalcase.exception.ProjectNotFoundException;
import com.definex.finalcase.exception.UserNotFoundException;
import com.definex.finalcase.mapper.ProjectMapper;
import com.definex.finalcase.mapper.UserMapper;
import com.definex.finalcase.repository.ProjectRepository;
import com.definex.finalcase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final UserMapper userMapper;

    public ProjectResponse createProject(ProjectRequest request) {
        Project project = projectMapper.toEntity(request);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    public ProjectResponse getProjectById(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(ProjectNotFoundException::new);
        return projectMapper.toResponse(project);
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    public ProjectResponse updateProject(UUID id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(ProjectNotFoundException::new);

        project.setTitle(request.title());
        project.setDescription(request.description());
        project.setDepartmentName(request.departmentName());
        project.setStatus(request.status());

        return projectMapper.toResponse(projectRepository.save(project));
    }

    public void deleteProject(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(ProjectNotFoundException::new);
        projectRepository.delete(project);
        projectRepository.save(project);
    }

    public ProjectResponse assignUserToProject(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(ProjectNotFoundException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        project.getTeamMembers().add(user);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    public ProjectResponse removeUserFromProject(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(ProjectNotFoundException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        project.getTeamMembers().remove(user);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    public List<UserResponse> getProjectTeam(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(ProjectNotFoundException::new);

        return project.getTeamMembers()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

}