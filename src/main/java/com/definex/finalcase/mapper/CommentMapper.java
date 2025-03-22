package com.definex.finalcase.mapper;

import com.definex.finalcase.domain.entity.Comment;
import com.definex.finalcase.domain.request.CommentRequest;
import com.definex.finalcase.domain.response.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CommentMapper {
    Comment toEntity(CommentRequest request);

    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "user.id", target = "userId")
    CommentResponse toResponse(Comment comment);
}
