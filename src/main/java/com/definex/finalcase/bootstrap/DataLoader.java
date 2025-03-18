package com.definex.finalcase.bootstrap;

import com.definex.finalcase.domain.entity.Project;
import com.definex.finalcase.domain.entity.Task;
import com.definex.finalcase.domain.entity.TaskStateChange;
import com.definex.finalcase.domain.entity.User;
import com.definex.finalcase.domain.enums.ProjectStatus;
import com.definex.finalcase.domain.enums.Role;
import com.definex.finalcase.domain.enums.TaskPriority;
import com.definex.finalcase.domain.enums.TaskState;
import com.definex.finalcase.repository.ProjectRepository;
import com.definex.finalcase.repository.TaskRepository;
import com.definex.finalcase.repository.TaskStateChangeRepository;
import com.definex.finalcase.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskStateChangeRepository taskStateChangeRepository;

    @Override
    @Transactional
    public void run(String... args) {
        loadUsers();
        loadProjectsAndTasks();
    }

    private void loadUsers() {
        if (userRepository.count() > 0) {
            log.info("Users already exist, skipping initialization.");
            return;
        }

        log.info("Initializing Users...");

        User alice = new User();
        alice.setEmail("alice@example.com");
        alice.setPassword("{noop}password123");
        alice.setName("Alice Johnson");
        alice.setRole(Role.PROJECT_MANAGER);

        User bob = new User();
        bob.setEmail("bob@example.com");
        bob.setPassword("{noop}password123");
        bob.setName("Bob Smith");
        bob.setRole(Role.TEAM_MEMBER);

        userRepository.saveAll(List.of(alice, bob));
        log.info("Users initialized.");
    }

    private void loadProjectsAndTasks() {
        if (projectRepository.count() > 0) {
            log.info("Projects already exist, skipping initialization.");
            return;
        }

        log.info("Initializing Projects & Tasks...");

        User alice = userRepository.findByEmail("alice@example.com")
                .orElseThrow(() -> new RuntimeException("Alice user not found!"));
        User bob = userRepository.findByEmail("bob@example.com")
                .orElseThrow(() -> new RuntimeException("Bob user not found!"));

        Project project = new Project();
        project.setTitle("Task Management System");
        project.setDescription("A system for managing tasks and tracking their states.");
        project.setDepartmentName("Engineering");
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setTeamMembers(List.of(alice, bob));

        project = projectRepository.save(project);

        Task task1 = new Task();
        task1.setTitle("Setup Backend Architecture");
        task1.setDescription("Initialize Spring Boot project with dependencies.");
        task1.setState(TaskState.BACKLOG);
        task1.setPriority(TaskPriority.HIGH);
        task1.setProject(project);
        task1.setAssignee(alice);

        Task task2 = new Task();
        task2.setTitle("Implement Authentication");
        task2.setDescription("Setup JWT authentication.");
        task2.setState(TaskState.IN_ANALYSIS);
        task2.setPriority(TaskPriority.MEDIUM);
        task2.setProject(project);
        task2.setAssignee(bob);

        taskRepository.saveAll(List.of(task1, task2));

        TaskStateChange stateChange = new TaskStateChange();
        stateChange.setTask(task2);
        stateChange.setChangedBy(alice);
        stateChange.setOldState(TaskState.BACKLOG);
        stateChange.setNewState(TaskState.IN_ANALYSIS);
        stateChange.setReason("Requirements gathered.");

        taskStateChangeRepository.save(stateChange);

    }
}