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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

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

        User gokhan = new User();
        gokhan.setEmail("gokhan.tamkoc@example.com");
        gokhan.setPassword(passwordEncoder.encode("gokhanpassword"));
        gokhan.setName("Gökhan Tamkoç");
        gokhan.setRole(Role.PROJECT_MANAGER);

        User enver = new User();
        enver.setEmail("enver.yigitler@example.com");
        enver.setPassword(passwordEncoder.encode("enverpassword"));
        enver.setName("Enver Yiğitler");
        enver.setRole(Role.TEAM_MEMBER);

        userRepository.saveAll(List.of(gokhan, enver));
        log.info("Users initialized.");
    }

    private void loadProjectsAndTasks() {
        if (projectRepository.count() > 0) {
            log.info("Projects already exist, skipping initialization.");
            return;
        }

        log.info("Initializing Projects & Tasks...");

        User gokhan = userRepository.findByEmail("gokhan.tamkoc@example.com")
                .orElseThrow(() -> new RuntimeException("Gökhan user not found!"));
        User enver = userRepository.findByEmail("enver.yigitler@example.com")
                .orElseThrow(() -> new RuntimeException("Enver user not found!"));

        Project project = new Project();
        project.setTitle("Görev Yönetim Sistemi");
        project.setDescription("Görevlerin yönetildiği ve durumlarının takip edildiği bir sistem.");
        project.setDepartmentName("Mühendislik");
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setTeamMembers(List.of(gokhan, enver));

        project = projectRepository.save(project);

        Task task1 = new Task();
        task1.setTitle("Backend Mimarisi Kurulumu");
        task1.setDescription("Spring Boot projesini gerekli bağımlılıklarla başlat.");
        task1.setState(TaskState.BACKLOG);
        task1.setPriority(TaskPriority.HIGH);
        task1.setProject(project);
        task1.setAssignee(gokhan);

        Task task2 = new Task();
        task2.setTitle("Kimlik Doğrulama Sistemi");
        task2.setDescription("JWT ile kimlik doğrulama sistemini kur.");
        task2.setState(TaskState.IN_ANALYSIS);
        task2.setPriority(TaskPriority.MEDIUM);
        task2.setProject(project);
        task2.setAssignee(enver);

        taskRepository.saveAll(List.of(task1, task2));

        TaskStateChange stateChange = new TaskStateChange();
        stateChange.setTask(task2);
        stateChange.setChangedBy(gokhan);
        stateChange.setOldState(TaskState.BACKLOG);
        stateChange.setNewState(TaskState.IN_ANALYSIS);
        stateChange.setReason("Gereksinimler toplandı.");

        taskStateChangeRepository.save(stateChange);
    }
}