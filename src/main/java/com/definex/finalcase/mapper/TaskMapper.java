package com.definex.finalcase.mapper;

import com.definex.finalcase.domain.entity.Task;
import com.definex.finalcase.domain.request.TaskRequest;
import com.definex.finalcase.domain.response.TaskResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface TaskMapper {

    Task toEntity(TaskRequest request);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "assignee.id", target = "assigneeId")
    TaskResponse toResponse(Task task);
}
