package com.definex.finalcase.mapper;

import com.definex.finalcase.domain.entity.Task;
import com.definex.finalcase.domain.entity.TaskStateChange;
import com.definex.finalcase.domain.request.TaskRequest;
import com.definex.finalcase.domain.response.TaskResponse;
import com.definex.finalcase.domain.response.TaskStateChangeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface TaskStateChangeMapper {

    @Mapping(source = "changedBy.id", target = "changedByUserId")
    @Mapping(source = "task.id", target = "taskId")
    TaskStateChangeResponse toResponse(TaskStateChange taskStateChange);
}