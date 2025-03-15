package com.definex.finalcase.domain.entity;

import com.definex.finalcase.domain.BaseEntity;
import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "attachments")
public class Attachment extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "uploadedBy_id", nullable = false)
    private User uploadedBy;

    @NotBlank
    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private boolean deleted = false;

    public void softDelete() {
        this.deleted = true;
    }
}