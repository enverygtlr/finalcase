package com.definex.finalcase.service;

import com.definex.finalcase.domain.entity.Project;
import com.definex.finalcase.domain.entity.Task;
import com.definex.finalcase.domain.entity.TaskStateChange;
import com.definex.finalcase.domain.entity.User;
import com.definex.finalcase.domain.enums.TaskPriority;
import com.definex.finalcase.domain.enums.TaskState;
import com.definex.finalcase.domain.request.ReasonRequest;
import com.definex.finalcase.domain.request.TaskDescriptionRequest;
import com.definex.finalcase.domain.request.TaskRequest;
import com.definex.finalcase.domain.response.TaskResponse;
import com.definex.finalcase.domain.response.TaskStateChangeResponse;
import com.definex.finalcase.exception.ProjectNotFoundException;
import com.definex.finalcase.exception.TaskNotFoundException;
import com.definex.finalcase.exception.UserNotFoundException;
import com.definex.finalcase.mapper.TaskMapper;
import com.definex.finalcase.mapper.TaskStateChangeMapper;
import com.definex.finalcase.repository.ProjectRepository;
import com.definex.finalcase.repository.TaskRepository;
import com.definex.finalcase.repository.TaskStateChangeRepository;
import com.definex.finalcase.repository.UserRepository;
import com.definex.finalcase.validation.TaskStateValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskStateChangeRepository taskStateChangeRepository;

    @Mock
    private TaskStateChangeMapper taskStateChangeMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskStateValidator taskStateValidator;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_shouldSucceed() {
        // Given
        UUID projectId = UUID.randomUUID();
        TaskRequest request = TaskRequest.builder()
                .title("Task Title")
                .description("Description")
                .acceptanceCriteria("Acceptance Criteria")
                .priority(TaskPriority.HIGH)
                .state(TaskState.BACKLOG)
                .build();

        Project project = new Project();
        Task task = new Task();
        Task savedTask = new Task();
        TaskResponse expectedResponse = Mockito.mock(TaskResponse.class);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskMapper.toEntity(request)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskMapper.toResponse(savedTask)).thenReturn(expectedResponse);

        // When
        TaskResponse actualResponse = taskService.createTask(projectId, request);

        // Then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void createTask_shouldThrowExceptionWhenProjectNotFound() {
        // Given
        UUID projectId = UUID.randomUUID();
        TaskRequest request = Mockito.mock(TaskRequest.class);

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(ProjectNotFoundException.class, () -> taskService.createTask(projectId, request));
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void updateTaskState_shouldSucceed() {
        // Given
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Task task = new Task();
        task.setState(TaskState.BACKLOG);

        User user = new User();
        TaskState newState = TaskState.IN_ANALYSIS;
        String reason = null;

        ReasonRequest reasonRequest = ReasonRequest.builder().reason(reason).build();
        TaskStateChange savedChange = new TaskStateChange();
        TaskStateChangeResponse expectedResponse = Mockito.mock(TaskStateChangeResponse.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        doNothing().when(taskStateValidator).validateStateTransition(TaskState.BACKLOG, newState, reason);
        when(taskStateChangeRepository.save(any())).thenReturn(savedChange);
        when(taskStateChangeMapper.toResponse(savedChange)).thenReturn(expectedResponse);

        // When
        TaskStateChangeResponse actualResponse = taskService.updateTaskState(taskId, newState, userId, reasonRequest);

        // Then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void updateTaskState_shouldThrowWhenTaskNotFound() {
        // Given
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ReasonRequest reasonRequest = ReasonRequest.builder().reason("Reason").build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTaskState(taskId, TaskState.BLOCKED, userId, reasonRequest));
    }

    @Test
    void updateTaskState_shouldThrowWhenUserNotFound() {
        // Given
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Task task = new Task();
        task.setState(TaskState.BACKLOG);
        ReasonRequest reasonRequest = ReasonRequest.builder().reason("Reason").build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(UserNotFoundException.class, () -> taskService.updateTaskState(taskId, TaskState.BLOCKED, userId, reasonRequest));
    }


    @Test
    void getTaskById_shouldReturnTaskResponse() {
        // Given
        UUID taskId = UUID.randomUUID();
        Task task = new Task();
        TaskResponse expected = Mockito.mock(TaskResponse.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(expected);

        // When
        TaskResponse actual = taskService.getTaskById(taskId);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void getTaskById_shouldThrowWhenTaskNotFound() {
        // Given
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(taskId));
    }


    @Test
    void getAllTasksForProject_shouldReturnTaskResponses() {
        // Given
        UUID projectId = UUID.randomUUID();
        Task task1 = new Task();
        Task task2 = new Task();
        List<Task> tasks = List.of(task1, task2);

        TaskResponse response1 = Mockito.mock(TaskResponse.class);
        TaskResponse response2 = Mockito.mock(TaskResponse.class);

        when(taskRepository.findByProjectId(projectId)).thenReturn(tasks);
        when(taskMapper.toResponse(task1)).thenReturn(response1);
        when(taskMapper.toResponse(task2)).thenReturn(response2);

        // When
        List<TaskResponse> result = taskService.getAllTasksForProject(projectId);

        // Then
        assertEquals(List.of(response1, response2), result);
        verify(taskRepository, times(1)).findByProjectId(projectId);
    }

    @Test
    void updateTaskDescription_shouldSucceed() {
        // Given
        UUID taskId = UUID.randomUUID();
        String newDescription = "Updated task description";
        TaskDescriptionRequest request = TaskDescriptionRequest.builder().description(newDescription).build();

        Task task = new Task();
        Task savedTask = new Task();
        TaskResponse expected = Mockito.mock(TaskResponse.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskMapper.toResponse(savedTask)).thenReturn(expected);

        // When
        TaskResponse actual = taskService.updateTaskDescription(taskId, request);

        // Then
        assertEquals(expected, actual);
        assertEquals(newDescription, task.getDescription());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void updateTaskDescription_shouldThrowWhenTaskNotFound() {
        // Given
        UUID taskId = UUID.randomUUID();
        TaskDescriptionRequest request = TaskDescriptionRequest.builder().description("irrelevant").build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTaskDescription(taskId, request));
    }

    @Test
    void assignUserToTask_shouldSucceed() {
        // Given
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Task task = new Task();
        User user = new User();
        Task savedTask = new Task();
        TaskResponse expected = Mockito.mock(TaskResponse.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskMapper.toResponse(savedTask)).thenReturn(expected);

        // When
        TaskResponse actual = taskService.assignUserToTask(taskId, userId);

        // Then
        assertEquals(expected, actual);
        assertEquals(user, task.getAssignee());
        verify(taskRepository, times(1)).save(task);
    }


    @Test
    void assignUserToTask_shouldThrowWhenUserNotFound() {
        // Given
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Task task = new Task();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(UserNotFoundException.class, () -> taskService.assignUserToTask(taskId, userId));
    }

    @Test
    void updateTaskPriority_shouldSucceed() {
        // Given
        UUID taskId = UUID.randomUUID();
        Task task = new Task();
        Task savedTask = new Task();
        TaskResponse expected = Mockito.mock(TaskResponse.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskMapper.toResponse(savedTask)).thenReturn(expected);

        // When
        TaskResponse actual = taskService.updateTaskPriority(taskId, TaskPriority.CRITICAL);

        // Then
        assertEquals(expected, actual);
        assertEquals(TaskPriority.CRITICAL, task.getPriority());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void updateTaskPriority_shouldThrowWhenTaskNotFound() {
        // Given
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When - Then
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTaskPriority(taskId, TaskPriority.LOW));
    }
}
