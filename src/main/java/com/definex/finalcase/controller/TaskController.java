package com.definex.finalcase.controller;

import com.definex.finalcase.domain.enums.TaskPriority;
import com.definex.finalcase.domain.enums.TaskState;
import com.definex.finalcase.domain.request.TaskRequest;
import com.definex.finalcase.domain.response.TaskResponse;
import com.definex.finalcase.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

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
}