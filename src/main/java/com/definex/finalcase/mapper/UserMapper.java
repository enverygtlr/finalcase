package com.definex.finalcase.mapper;

import com.definex.finalcase.domain.entity.User;
import com.definex.finalcase.domain.request.UserRequest;
import com.definex.finalcase.domain.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequest request);

    UserResponse toResponse(User user);
}
