package com.definex.finalcase.mapper;

import com.definex.finalcase.domain.entity.Comment;
import com.definex.finalcase.domain.request.CommentRequest;
import com.definex.finalcase.domain.response.CommentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toEntity(CommentRequest request);
    CommentResponse toResponse(Comment comment);
}
