package com.definex.finalcase.mapper;

import com.definex.finalcase.domain.entity.Attachment;
import com.definex.finalcase.domain.response.AttachmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AttachmentMapper {
    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "uploadedBy.id", target = "uploadedById")
    AttachmentResponse toResponse(Attachment attachment);
}