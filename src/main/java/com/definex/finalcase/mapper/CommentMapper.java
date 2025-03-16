package com.definex.finalcase.mapper;

import com.definex.finalcase.domain.entity.Comment;
import com.definex.finalcase.domain.request.CommentRequest;
import com.definex.finalcase.domain.response.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CommentMapper {
    Comment toEntity(CommentRequest request);
    CommentResponse toResponse(Comment comment);
}
