package com.definex.finalcase.controller;

import com.definex.finalcase.domain.entity.User;
import com.definex.finalcase.domain.enums.TaskPriority;
import com.definex.finalcase.domain.enums.TaskState;
import com.definex.finalcase.domain.request.CommentRequest;
import com.definex.finalcase.domain.request.ReasonRequest;
import com.definex.finalcase.domain.request.TaskDescriptionRequest;
import com.definex.finalcase.domain.request.TaskRequest;
import com.definex.finalcase.domain.response.*;
import com.definex.finalcase.security.UserPrincipal;
import com.definex.finalcase.security.permission.AnyTeamRole;
import com.definex.finalcase.security.permission.ManagerOrLeaderRole;
import com.definex.finalcase.service.AttachmentService;
import com.definex.finalcase.service.CommentService;
import com.definex.finalcase.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final CommentService commentService;
    private final AttachmentService attachmentService;

    @ManagerOrLeaderRole
    @PostMapping("/{projectId}")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @RequestBody @Valid TaskRequest taskRequest) {
        TaskResponse createdTask = taskService.createTask(projectId, taskRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @AnyTeamRole
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID taskId) {
        TaskResponse taskResponse = taskService.getTaskById(taskId);
        return ResponseEntity.ok(taskResponse);
    }

    @AnyTeamRole
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponse>> getAllTasksForProject(@PathVariable UUID projectId) {
        List<TaskResponse> tasks = taskService.getAllTasksForProject(projectId);
        return ResponseEntity.ok(tasks);
    }

    @AnyTeamRole
    @PatchMapping("/{taskId}/state/{newState}")
    public ResponseEntity<TaskStateChangeResponse> updateTaskState(
            @PathVariable UUID taskId,
            @PathVariable TaskState newState,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody(required = false) ReasonRequest request) {
        TaskStateChangeResponse taskStateChangeResponse = taskService.updateTaskState(taskId, newState, user.getUserId(), request);
        return ResponseEntity.ok(taskStateChangeResponse);
    }

    @ManagerOrLeaderRole
    @PatchMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<TaskResponse> assignUserToTask(
            @PathVariable UUID taskId,
            @PathVariable UUID userId) {
        TaskResponse updatedTask = taskService.assignUserToTask(taskId, userId);
        return ResponseEntity.ok(updatedTask);
    }

    @ManagerOrLeaderRole
    @PatchMapping("/{taskId}/priority/{priority}")
    public ResponseEntity<TaskResponse> updateTaskPriority(
            @PathVariable UUID taskId,
            @PathVariable TaskPriority priority) {
        TaskResponse updatedTask = taskService.updateTaskPriority(taskId, priority);
        return ResponseEntity.ok(updatedTask);
    }

    @ManagerOrLeaderRole
    @PatchMapping("/{taskId}/description")
    public ResponseEntity<TaskResponse> updateTaskDescription(@PathVariable UUID taskId, @RequestBody TaskDescriptionRequest taskDescriptionRequest) {
        TaskResponse updatedTask = taskService.updateTaskDescription(taskId, taskDescriptionRequest);
        return ResponseEntity.ok(updatedTask);
    }

    //COMMENTS
    @AnyTeamRole
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal  UserPrincipal user,
            @RequestBody @Valid CommentRequest request) {
        CommentResponse response = commentService.addComment(taskId, user.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AnyTeamRole
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsForTask(@PathVariable UUID taskId) {
        return ResponseEntity.ok(commentService.getCommentsForTask(taskId));
    }

    @AnyTeamRole
    @GetMapping("/{taskId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(
            @PathVariable UUID taskId, @PathVariable UUID commentId) {
        return ResponseEntity.ok(commentService.getCommentById(commentId));
    }

    @AnyTeamRole
    @PatchMapping("/{taskId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable UUID taskId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid CommentRequest request) {
        return ResponseEntity.ok(commentService.updateComment(commentId, user.getUserId(), request));
    }

    @AnyTeamRole
    @DeleteMapping("/{taskId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID taskId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal UserPrincipal user) {
        commentService.deleteComment(commentId, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    //ATTACHMENTS
    @AnyTeamRole
    @PostMapping(value = "/{taskId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentResponse> uploadAttachment(@PathVariable UUID taskId,
                                                   @AuthenticationPrincipal UserPrincipal user,
                                                   @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(attachmentService.uploadAttachment(taskId, user.getUserId(), file));
    }

    @AnyTeamRole
    @GetMapping("/{taskId}/attachments")
    public ResponseEntity<List<AttachmentResponse>> getAttachments(@PathVariable UUID taskId) {
        return ResponseEntity.ok(attachmentService.getAttachmentsByTask(taskId));
    }

    @AnyTeamRole
    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable UUID attachmentId) {
        FileResponse fileResponse = attachmentService.downloadAttachment(attachmentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileResponse.mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResponse.fileName() + "\"")
                .body(fileResponse.data());
    }

    @AnyTeamRole
    @DeleteMapping("/{taskId}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable UUID attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}