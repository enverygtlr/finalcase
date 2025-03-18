package com.definex.finalcase.controller;

import com.definex.finalcase.domain.enums.TaskPriority;
import com.definex.finalcase.domain.enums.TaskState;
import com.definex.finalcase.domain.request.CommentRequest;
import com.definex.finalcase.domain.request.TaskRequest;
import com.definex.finalcase.domain.response.AttachmentResponse;
import com.definex.finalcase.domain.response.CommentResponse;
import com.definex.finalcase.domain.response.FileResponse;
import com.definex.finalcase.domain.response.TaskResponse;
import com.definex.finalcase.service.AttachmentService;
import com.definex.finalcase.service.CommentService;
import com.definex.finalcase.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/{projectId}")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @RequestBody @Valid TaskRequest taskRequest) {
        TaskResponse createdTask = taskService.createTask(projectId, taskRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID taskId) {
        TaskResponse taskResponse = taskService.getTaskById(taskId);
        return ResponseEntity.ok(taskResponse);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponse>> getAllTasksForProject(@PathVariable UUID projectId) {
        List<TaskResponse> tasks = taskService.getAllTasksForProject(projectId);
        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/{taskId}/state/{newState}")
    public ResponseEntity<TaskResponse> updateTaskState(
            @PathVariable UUID taskId,
            @PathVariable TaskState newState,
            @RequestParam UUID userId,
            @RequestParam(required = false) String reason) {
        TaskResponse updatedTask = taskService.updateTaskState(taskId, newState, userId, reason);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<TaskResponse> assignUserToTask(
            @PathVariable UUID taskId,
            @PathVariable UUID userId) {
        TaskResponse updatedTask = taskService.assignUserToTask(taskId, userId);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{taskId}/priority/{priority}")
    public ResponseEntity<TaskResponse> updateTaskPriority(
            @PathVariable UUID taskId,
            @PathVariable TaskPriority priority) {
        TaskResponse updatedTask = taskService.updateTaskPriority(taskId, priority);
        return ResponseEntity.ok(updatedTask);
    }

    //COMMENTS
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable UUID taskId,
            @RequestParam UUID userId,
            @RequestBody @Valid CommentRequest request) {
        CommentResponse response = commentService.addComment(taskId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsForTask(@PathVariable UUID taskId) {
        return ResponseEntity.ok(commentService.getCommentsForTask(taskId));
    }

    @GetMapping("/{taskId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(
            @PathVariable UUID taskId, @PathVariable UUID commentId) {
        return ResponseEntity.ok(commentService.getCommentById(commentId));
    }

    @PatchMapping("/{taskId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable UUID taskId,
            @PathVariable UUID commentId,
            @RequestParam UUID userId,
            @RequestBody @Valid CommentRequest request) {
        return ResponseEntity.ok(commentService.updateComment(commentId, userId, request));
    }

    @DeleteMapping("/{taskId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID taskId,
            @PathVariable UUID commentId,
            @RequestParam UUID userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    //ATTACHMENTS

    @PostMapping(value = "/{taskId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentResponse> uploadAttachment(@PathVariable UUID taskId,
                                                   @RequestParam UUID userId,
                                                   @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(attachmentService.uploadAttachment(taskId, userId, file));
    }

    @GetMapping("/{taskId}/attachments")
    public ResponseEntity<List<AttachmentResponse>> getAttachments(@PathVariable UUID taskId) {
        return ResponseEntity.ok(attachmentService.getAttachmentsByTask(taskId));
    }

    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable UUID attachmentId) {
        FileResponse fileResponse = attachmentService.downloadAttachment(attachmentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileResponse.mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResponse.fileName() + "\"")
                .body(fileResponse.data());
    }

    @DeleteMapping("/{taskId}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable UUID attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}