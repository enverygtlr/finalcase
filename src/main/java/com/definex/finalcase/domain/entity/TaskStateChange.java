package com.definex.finalcase.domain.entity;

import com.definex.finalcase.domain.BaseEntity;
import com.definex.finalcase.domain.enums.TaskState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "task_state_changes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskStateChange extends BaseEntity {

    @ManyToOne
    @NotFound(action = NotFoundAction.EXCEPTION)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne
    @NotFound(action = NotFoundAction.EXCEPTION)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskState oldState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskState newState;

    @Column(columnDefinition = "TEXT")
    private String reason;
}
