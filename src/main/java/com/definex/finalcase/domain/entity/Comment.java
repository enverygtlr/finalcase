package com.definex.finalcase.domain.entity;

import com.definex.finalcase.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Comment extends BaseEntity {
    @ManyToOne
    @NotFound(action = NotFoundAction.EXCEPTION)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne
    @NotFound(action = NotFoundAction.EXCEPTION)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

}
