package com.definex.finalcase.service;

import com.definex.finalcase.domain.entity.Project;
import com.definex.finalcase.domain.entity.User;
import com.definex.finalcase.domain.enums.ProjectStatus;
import com.definex.finalcase.domain.request.ProjectRequest;
import com.definex.finalcase.domain.response.ProjectResponse;
import com.definex.finalcase.domain.response.UserResponse;
import com.definex.finalcase.exception.ProjectNotFoundException;
import com.definex.finalcase.exception.UserDuplicateInProjectException;
import com.definex.finalcase.exception.UserNotFoundException;
import com.definex.finalcase.mapper.ProjectMapper;
import com.definex.finalcase.mapper.UserMapper;
import com.definex.finalcase.repository.ProjectRepository;
import com.definex.finalcase.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProject_shouldSucceed() {
        // Given
        ProjectRequest request = ProjectRequest.builder()
                .title("New Project")
                .description("Project Description")
                .departmentName("Department Name")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        Project project = new Project();
        Project savedProject = new Project();
        ProjectResponse expected = Mockito.mock(ProjectResponse.class);

        when(projectMapper.toEntity(request)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(savedProject);
        when(projectMapper.toResponse(savedProject)).thenReturn(expected);

        // When
        var actual = projectService.createProject(request);

        // Then
        assertEquals(expected, actual);
        verify(projectMapper).toEntity(request);
        verify(projectRepository).save(project);
        verify(projectMapper).toResponse(savedProject);
    }

    @Test
    void getProjectById_shouldReturnProjectResponse_whenExists() {
        // Given
        UUID id = UUID.randomUUID();
        Project project = new Project();
        ProjectResponse expected = Mockito.mock(ProjectResponse.class);

        when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(projectMapper.toResponse(project)).thenReturn(expected);

        // When
        var actual = projectService.getProjectById(id);

        // Then
        assertEquals(expected, actual);
        verify(projectRepository).findById(id);
        verify(projectMapper).toResponse(project);
    }

    @Test
    void getProjectById_shouldThrow_whenNotFound() {
        // Given
        UUID id = UUID.randomUUID();
        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(id));
    }

    @Test
    void getAllProjects_shouldReturnMappedResponses() {
        // Given
        Project p1 = new Project();
        Project p2 = new Project();
        List<Project> projects = List.of(p1, p2);

        ProjectResponse r1 = Mockito.mock(ProjectResponse.class);
        ProjectResponse r2 = Mockito.mock(ProjectResponse.class);

        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.toResponse(p1)).thenReturn(r1);
        when(projectMapper.toResponse(p2)).thenReturn(r2);

        // When
        List<ProjectResponse> result = projectService.getAllProjects();

        // Then
        assertEquals(List.of(r1, r2), result);
        verify(projectRepository).findAll();
    }

    @Test
    void updateProject_shouldSucceed() {
        // Given
        UUID id = UUID.randomUUID();
        ProjectRequest request = ProjectRequest.builder()
                .title("Updated Title")
                .description("Updated Desc")
                .departmentName("Updated Department Name")
                .status(ProjectStatus.COMPLETED)
                .build();

        Project project = new Project();
        Project savedProject = new Project();
        ProjectResponse expected = Mockito.mock(ProjectResponse.class);

        when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(savedProject);
        when(projectMapper.toResponse(savedProject)).thenReturn(expected);

        // When
        var actual = projectService.updateProject(id, request);

        // Then
        assertEquals(expected, actual);
        assertEquals("Updated Title", project.getTitle());
        assertEquals("Updated Desc", project.getDescription());
        assertEquals("Updated Department Name", project.getDepartmentName());
        assertEquals(ProjectStatus.COMPLETED, project.getStatus());
    }

    @Test
    void updateProject_shouldThrow_whenNotFound() {
        // Given
        UUID id = UUID.randomUUID();
        ProjectRequest request = Mockito.mock(ProjectRequest.class);

        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(ProjectNotFoundException.class, () -> projectService.updateProject(id, request));
    }

    @Test
    void deleteProject_shouldSucceed_whenProjectExists() {
        // Given
        UUID id = UUID.randomUUID();
        Project project = new Project();

        when(projectRepository.findById(id)).thenReturn(Optional.of(project));

        // When
        projectService.deleteProject(id);

        // Then
        verify(projectRepository).delete(project);
        verify(projectRepository).save(project);
    }

    @Test
    void deleteProject_shouldThrow_whenNotFound() {
        // Given
        UUID id = UUID.randomUUID();
        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(ProjectNotFoundException.class, () -> projectService.deleteProject(id));
    }

    @Test
    void assignUserToProject_shouldSucceed_whenUserNotAlreadyAssigned() {
        // Given
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        Project project = new Project();
        project.setTeamMembers(new ArrayList<>());

        Project savedProject = new Project();
        ProjectResponse expected = Mockito.mock(ProjectResponse.class);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.save(project)).thenReturn(savedProject);
        when(projectMapper.toResponse(savedProject)).thenReturn(expected);

        // When
        var actual = projectService.assignUserToProject(projectId, userId);

        // Then
        assertEquals(expected, actual);
        assertTrue(project.getTeamMembers().contains(user));
    }

    @Test
    void assignUserToProject_shouldThrow_whenUserAlreadyAssigned() {
        // Given
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        Project project = new Project();
        project.setTeamMembers(new ArrayList<>(List.of(user)));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When - Then
        assertThrows(UserDuplicateInProjectException.class, () -> projectService.assignUserToProject(projectId, userId));
    }

    @Test
    void assignUserToProject_shouldThrow_whenProjectNotFound() {
        // Given
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(ProjectNotFoundException.class, () -> projectService.assignUserToProject(projectId, userId));
    }

    @Test
    void assignUserToProject_shouldThrow_whenUserNotFound() {
        // Given
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Project project = new Project();
        project.setTeamMembers(new ArrayList<>());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(UserNotFoundException.class, () -> projectService.assignUserToProject(projectId, userId));
    }

    @Test
    void removeUserFromProject_shouldSucceed() {
        // Given
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        Project project = new Project();
        project.setTeamMembers(new ArrayList<>(List.of(user)));

        Project savedProject = new Project();
        ProjectResponse expected = Mockito.mock(ProjectResponse.class);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.save(project)).thenReturn(savedProject);
        when(projectMapper.toResponse(savedProject)).thenReturn(expected);

        // When
        var actual = projectService.removeUserFromProject(projectId, userId);

        // Then
        assertEquals(expected, actual);
        assertFalse(project.getTeamMembers().contains(user));
    }

    @Test
    void removeUserFromProject_shouldThrow_whenProjectNotFound() {
        // Given
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(ProjectNotFoundException.class, () -> projectService.removeUserFromProject(projectId, userId));
    }

    @Test
    void removeUserFromProject_shouldThrow_whenUserNotFound() {
        // Given
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Project project = new Project();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(UserNotFoundException.class, () -> projectService.removeUserFromProject(projectId, userId));
    }

    @Test
    void getProjectTeam_shouldReturnMappedUserResponses() {
        // Given
        UUID projectId = UUID.randomUUID();

        User u1 = new User();
        User u2 = new User();
        Project project = new Project();
        project.setTeamMembers(new ArrayList<>(List.of(u1, u2)));

        UserResponse r1 = Mockito.mock(UserResponse.class);
        UserResponse r2 = Mockito.mock(UserResponse.class);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userMapper.toResponse(u1)).thenReturn(r1);
        when(userMapper.toResponse(u2)).thenReturn(r2);

        // When
        var result = projectService.getProjectTeam(projectId);

        // Then
        assertEquals(List.of(r1, r2), result);
    }

    @Test
    void getProjectTeam_shouldThrow_whenProjectNotFound() {
        // Given
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectTeam(projectId));
    }
}

