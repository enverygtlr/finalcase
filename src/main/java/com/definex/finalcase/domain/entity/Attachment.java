package com.definex.finalcase.domain.entity;

import com.definex.finalcase.domain.BaseEntity;
import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.N;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "attachments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Attachment extends BaseEntity {

    @ManyToOne
    @NotFound(action = NotFoundAction.EXCEPTION)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne
    @NotFound(action = NotFoundAction.EXCEPTION)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String fileName;

    @Column(nullable = false)
    private String externalFileName;

    @NotBlank
    @Column(nullable = false)
    private String contentType;
}