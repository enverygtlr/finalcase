package com.definex.finalcase.repository;

import com.definex.finalcase.domain.entity.TaskStateChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskStateChangeRepository extends JpaRepository<TaskStateChange, UUID> {

    List<TaskStateChange> findByTaskId(UUID taskId);
}