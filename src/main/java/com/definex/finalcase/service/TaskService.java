package com.definex.finalcase.service;

import com.definex.finalcase.domain.entity.Project;
import com.definex.finalcase.domain.entity.Task;
import com.definex.finalcase.domain.entity.TaskStateChange;
import com.definex.finalcase.domain.entity.User;
import com.definex.finalcase.domain.enums.TaskPriority;
import com.definex.finalcase.domain.enums.TaskState;
import com.definex.finalcase.domain.request.TaskRequest;
import com.definex.finalcase.domain.response.TaskResponse;
import com.definex.finalcase.exception.ProjectNotFoundException;
import com.definex.finalcase.exception.TaskNotFoundException;
import com.definex.finalcase.exception.UserNotFoundException;
import com.definex.finalcase.mapper.TaskMapper;
import com.definex.finalcase.repository.ProjectRepository;
import com.definex.finalcase.repository.TaskRepository;
import com.definex.finalcase.repository.TaskStateChangeRepository;
import com.definex.finalcase.repository.UserRepository;
import com.definex.finalcase.validation.TaskStateValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskStateChangeRepository taskStateChangeRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final TaskStateValidator taskStateValidator;

    public TaskResponse createTask(UUID projectId, TaskRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(ProjectNotFoundException::new);

        Task task = taskMapper.toEntity(request);
        task.setProject(project);
        Task savedTask = taskRepository.save(task);

        return taskMapper.toResponse(savedTask);
    }

    public TaskResponse getTaskById(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);
        return taskMapper.toResponse(task);
    }

    public List<TaskResponse> getAllTasksForProject(UUID projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream().map(taskMapper::toResponse).toList();
    }

    @Transactional
    public TaskResponse updateTaskState(UUID taskId, TaskState newState, UUID userId, String reason) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        taskStateValidator.validateStateTransition(task.getState(), newState, reason);

        TaskState oldState = task.getState();
        task.setState(newState);
        taskRepository.save(task);

        TaskStateChange stateChange = new TaskStateChange();
        stateChange.setTask(task);
        stateChange.setChangedBy(user);
        stateChange.setOldState(oldState);
        stateChange.setNewState(newState);
        stateChange.setReason(reason);

        taskStateChangeRepository.save(stateChange);

        return taskMapper.toResponse(task);
    }

    public TaskResponse assignUserToTask(UUID taskId, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        task.setAssignee(user);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    public TaskResponse updateTaskPriority(UUID taskId, TaskPriority priority) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);

        task.setPriority(priority);
        return taskMapper.toResponse(taskRepository.save(task));
    }
}

