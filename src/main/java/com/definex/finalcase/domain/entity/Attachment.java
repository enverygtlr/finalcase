package com.definex.finalcase.domain.entity;

import com.definex.finalcase.domain.BaseEntity;
import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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