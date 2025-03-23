package com.definex.finalcase.controller;

import com.definex.finalcase.domain.enums.TaskPriority;
import com.definex.finalcase.domain.enums.TaskState;
import com.definex.finalcase.domain.request.CommentRequest;
import com.definex.finalcase.domain.request.TaskDescriptionRequest;
import com.definex.finalcase.domain.request.TaskRequest;
import com.definex.finalcase.domain.response.AttachmentResponse;
import com.definex.finalcase.domain.response.CommentResponse;
import com.definex.finalcase.domain.response.FileResponse;
import com.definex.finalcase.domain.response.TaskResponse;
import com.definex.finalcase.security.UserPrincipal;
import com.definex.finalcase.service.AttachmentService;
import com.definex.finalcase.service.CommentService;
import com.definex.finalcase.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private AttachmentService attachmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "TEAM_LEADER")
    void createTask_shouldSucceedForManagerOrLeader() throws Exception {
        UUID projectId = UUID.randomUUID();

        TaskRequest request = TaskRequest.builder()
                .title("Fix bug")
                .description("Fix login issue")
                .acceptanceCriteria("Login should succeed")
                .state(TaskState.BACKLOG)
                .priority(TaskPriority.CRITICAL)
                .build();

        TaskResponse response = TaskResponse.builder()
                .id(UUID.randomUUID())
                .title("Fix bug")
                .description("Fix login issue")
                .acceptanceCriteria("Login should succeed")
                .state(TaskState.BACKLOG)
                .priority(TaskPriority.CRITICAL)
                .projectId(projectId)
                .build();

        when(taskService.createTask(projectId, request)).thenReturn(response);

        mockMvc.perform(post("/api/tasks/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Fix bug"));
    }

    @Test
    @WithMockUser(roles = "TEAM_MEMBER")
    void getAllTasksForProject_shouldReturnTasks() throws Exception {
        UUID projectId = UUID.randomUUID();

        List<TaskResponse> tasks = List.of(
                TaskResponse.builder().id(UUID.randomUUID()).title("Task 1").projectId(projectId).build()
        );

        when(taskService.getAllTasksForProject(projectId)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task 1"));
    }

    @Test
    @WithMockUser(roles = "TEAM_MEMBER")
    void getTaskById_shouldReturnTask() throws Exception {
        UUID taskId = UUID.randomUUID();

        TaskResponse response = TaskResponse.builder()
                .id(taskId)
                .title("Bug fix")
                .build();

        when(taskService.getTaskById(taskId)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Bug fix"));
    }

    @Test
    @WithMockUser(roles = "TEAM_LEADER")
    void updateTaskPriority_shouldSucceedForManagerOrLeader() throws Exception {
        UUID taskId = UUID.randomUUID();

        TaskResponse response = TaskResponse.builder()
                .id(taskId)
                .priority(TaskPriority.HIGH)
                .build();

        when(taskService.updateTaskPriority(taskId, TaskPriority.HIGH)).thenReturn(response);

        mockMvc.perform(patch("/api/tasks/{taskId}/priority/{priority}", taskId, TaskPriority.HIGH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    @WithMockUser(roles = "TEAM_LEADER")
    void updateTaskDescription_shouldSucceedForManagerOrLeader() throws Exception {
        UUID taskId = UUID.randomUUID();

        TaskDescriptionRequest request = TaskDescriptionRequest.builder()
                .description("Updated description")
                .build();

        TaskResponse response = TaskResponse.builder()
                .id(taskId)
                .description("Updated description")
                .build();

        when(taskService.updateTaskDescription(taskId, request)).thenReturn(response);

        mockMvc.perform(patch("/api/tasks/{taskId}/description", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @WithMockUser(roles = "TEAM_LEADER")
    void assignUserToTask_shouldSucceedForManagerOrLeader() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        TaskResponse response = TaskResponse.builder()
                .id(taskId)
                .assigneeId(userId)
                .build();

        when(taskService.assignUserToTask(taskId, userId)).thenReturn(response);

        mockMvc.perform(patch("/api/tasks/{taskId}/assign/{userId}", taskId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assigneeId").value(userId.toString()));
    }

    @Test
    void addComment_shouldSucceedForTeamMember() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UserPrincipal principal = UserPrincipal.builder()
                .userId(userId)
                .email("user@example.com")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER")))
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        CommentRequest request = CommentRequest.builder().content("Nice work").build();
        CommentResponse response = CommentResponse.builder()
                .id(UUID.randomUUID())
                .taskId(taskId)
                .userId(userId)
                .content("Nice work")
                .build();

        when(commentService.addComment(taskId, userId, request)).thenReturn(response);

        mockMvc.perform(post("/api/tasks/{taskId}/comments", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Nice work"));
    }

    @Test
    @WithMockUser(roles = "TEAM_MEMBER")
    void getCommentsForTask_shouldReturnComments() throws Exception {
        UUID taskId = UUID.randomUUID();
        List<CommentResponse> comments = List.of(
                CommentResponse.builder().id(UUID.randomUUID()).content("Test comment").build()
        );

        when(commentService.getCommentsForTask(taskId)).thenReturn(comments);

        mockMvc.perform(get("/api/tasks/{taskId}/comments", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test comment"));
    }

    @Test
    @WithMockUser(roles = "TEAM_MEMBER")
    void updateComment_shouldSucceed() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UserPrincipal principal = UserPrincipal.builder()
                .userId(userId)
                .email("user@example.com")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER")))
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        CommentRequest request = CommentRequest.builder().content("Updated comment").build();
        CommentResponse response = CommentResponse.builder().id(commentId).content("Updated comment").build();

        when(commentService.updateComment(commentId, userId, request)).thenReturn(response);

        mockMvc.perform(patch("/api/tasks/{taskId}/comments/{commentId}", taskId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated comment"));
    }

    @Test
    @WithMockUser(roles = "TEAM_MEMBER")
    void deleteComment_shouldReturnNoContent() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UserPrincipal principal = UserPrincipal.builder()
                .userId(userId)
                .email("user@example.com")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER")))
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        doNothing().when(commentService).deleteComment(commentId, userId);

        mockMvc.perform(delete("/api/tasks/{taskId}/comments/{commentId}", taskId, commentId))
                .andExpect(status().isNoContent());
    }


    @Test
    void uploadAttachment_shouldReturnAttachmentResponse() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UserPrincipal principal = UserPrincipal.builder()
                .userId(userId)
                .email("user@example.com")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER")))
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "hello".getBytes());

        AttachmentResponse response = AttachmentResponse.builder()
                .id(UUID.randomUUID())
                .fileName("test.txt")
                .build();

        when(attachmentService.uploadAttachment(taskId, userId, file)).thenReturn(response);

        mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", taskId).file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test.txt"));
    }

    @Test
    @WithMockUser(roles = "TEAM_MEMBER")
    void getAttachments_shouldReturnList() throws Exception {
        UUID taskId = UUID.randomUUID();

        List<AttachmentResponse> responses = List.of(
                AttachmentResponse.builder().id(UUID.randomUUID()).fileName("file1.txt").build()
        );

        when(attachmentService.getAttachmentsByTask(taskId)).thenReturn(responses);

        mockMvc.perform(get("/api/tasks/{taskId}/attachments", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fileName").value("file1.txt"));
    }

    @Test
    @WithMockUser(roles = "TEAM_MEMBER")
    void downloadAttachment_shouldReturnFile() throws Exception {
        UUID attachmentId = UUID.randomUUID();

        FileResponse response = new FileResponse("file1.txt", MediaType.TEXT_PLAIN_VALUE, "content".getBytes());

        when(attachmentService.downloadAttachment(attachmentId)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/attachments/{attachmentId}", attachmentId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"file1.txt\""))
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().bytes("content".getBytes()));
    }

    @Test
    @WithMockUser(roles = "TEAM_MEMBER")
    void deleteAttachment_shouldReturnNoContent() throws Exception {
        UUID attachmentId = UUID.randomUUID();

        doNothing().when(attachmentService).deleteAttachment(attachmentId);

        mockMvc.perform(delete("/api/tasks/{taskId}/attachments/{attachmentId}", UUID.randomUUID(), attachmentId))
                .andExpect(status().isNoContent());
    }
}
