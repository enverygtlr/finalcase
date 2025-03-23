package com.definex.finalcase.controller;

import com.definex.finalcase.domain.enums.ProjectStatus;
import com.definex.finalcase.domain.enums.Role;
import com.definex.finalcase.domain.request.ProjectRequest;
import com.definex.finalcase.domain.response.ProjectResponse;
import com.definex.finalcase.domain.response.UserResponse;
import com.definex.finalcase.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void createProject_shouldSucceedForProjectManager() throws Exception {
        // Given
        ProjectRequest request = ProjectRequest.builder()
                .title("AI Research")
                .description("Exploring ML applications.")
                .departmentName("R&D")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        ProjectResponse response = ProjectResponse.builder()
                .id(UUID.randomUUID())
                .title("AI Research")
                .description("Exploring ML applications.")
                .departmentName("R&D")
                .status(ProjectStatus.IN_PROGRESS)
                .teamMembers(List.of())
                .build();

        when(projectService.createProject(request)).thenReturn(response);

        // When - Then
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("AI Research"));
    }

    @Test
    @WithMockUser(roles = "TEAM_MEMBER")
    void createProject_shouldReturnForbiddenForTeamMember() throws Exception {
        ProjectRequest request = ProjectRequest.builder()
                .title("Secret Project")
                .description("Classified work")
                .departmentName("Security")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TEAM_MEMBER")
    void getProjectById_shouldSucceedForAnyTeamRole() throws Exception {
        // Given
        UUID projectId = UUID.randomUUID();

        ProjectResponse response = ProjectResponse.builder()
                .id(projectId)
                .title("HR Automation")
                .description("Automate onboarding")
                .departmentName("HR")
                .status(ProjectStatus.COMPLETED)
                .teamMembers(List.of())
                .build();

        when(projectService.getProjectById(projectId)).thenReturn(response);

        // When - Then
        mockMvc.perform(get("/api/projects/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("HR Automation"));
    }

    @Test
    @WithMockUser(roles = "TEAM_MEMBER")
    void getAllProjects_shouldReturnProjectListForAnyTeamRole() throws Exception {
        // Given
        List<ProjectResponse> responseList = List.of(
                ProjectResponse.builder()
                        .id(UUID.randomUUID())
                        .title("Backend Migration")
                        .description("Move to Spring Boot 3")
                        .departmentName("IT")
                        .status(ProjectStatus.IN_PROGRESS)
                        .teamMembers(List.of())
                        .build()
        );

        when(projectService.getAllProjects()).thenReturn(responseList);

        // When - Then
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Backend Migration"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void updateProject_shouldSucceedForProjectManager() throws Exception {
        // Given
        UUID projectId = UUID.randomUUID();

        ProjectRequest request = ProjectRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .departmentName("Updated Dept")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        ProjectResponse response = ProjectResponse.builder()
                .id(projectId)
                .title("Updated Title")
                .description("Updated Description")
                .departmentName("Updated Dept")
                .status(ProjectStatus.IN_PROGRESS)
                .teamMembers(List.of())
                .build();

        when(projectService.updateProject(projectId, request)).thenReturn(response);

        // When - Then
        mockMvc.perform(patch("/api/projects/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void deleteProject_shouldSucceedForProjectManager() throws Exception {
        // Given
        UUID projectId = UUID.randomUUID();
        doNothing().when(projectService).deleteProject(projectId);

        // When - Then
        mockMvc.perform(delete("/api/projects/{projectId}", projectId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void assignUserToProject_shouldSucceedForProjectManager() throws Exception {
        // Given
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ProjectResponse response = ProjectResponse.builder()
                .id(projectId)
                .title("Project A")
                .description("Test")
                .departmentName("Dev")
                .status(ProjectStatus.IN_PROGRESS)
                .teamMembers(List.of(userId))
                .build();

        when(projectService.assignUserToProject(projectId, userId)).thenReturn(response);

        // When - Then
        mockMvc.perform(patch("/api/projects/{projectId}/assign/{userId}", projectId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamMembers[0]").value(userId.toString()));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void removeUserFromProject_shouldSucceedForProjectManager() throws Exception {
        // Given
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ProjectResponse response = ProjectResponse.builder()
                .id(projectId)
                .title("Project B")
                .description("Test")
                .departmentName("Dev")
                .status(ProjectStatus.IN_PROGRESS)
                .teamMembers(List.of()) // user removed
                .build();

        when(projectService.removeUserFromProject(projectId, userId)).thenReturn(response);

        // When - Then
        mockMvc.perform(patch("/api/projects/{projectId}/remove/{userId}", projectId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamMembers").isEmpty());
    }

    @Test
    @WithMockUser(roles = "TEAM_MEMBER")
    void getProjectTeam_shouldReturnUserListForAnyTeamRole() throws Exception {
        // Given
        UUID projectId = UUID.randomUUID();

        List<UserResponse> response = List.of(
                UserResponse.builder()
                        .id(UUID.randomUUID())
                        .name("Enver")
                        .email("enver@example.com")
                        .role(Role.TEAM_MEMBER)
                        .build()
        );

        when(projectService.getProjectTeam(projectId)).thenReturn(response);

        // When - Then
        mockMvc.perform(get("/api/projects/{projectId}/team", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Enver"));
    }
}

