package com.definex.finalcase.mapper;

import com.definex.finalcase.domain.entity.Project;
import com.definex.finalcase.domain.entity.User;
import com.definex.finalcase.domain.request.ProjectRequest;
import com.definex.finalcase.domain.response.ProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ProjectMapper {
    Project toEntity(ProjectRequest request);

    @Mapping(target = "teamMembers", source = "teamMembers", qualifiedByName = "mapUsersToUUIDs")
    ProjectResponse toResponse(Project project);

    @Named("mapUsersToUUIDs")
    default List<UUID> mapUsersToUUIDs(List<User> users) {
        return users == null ? Collections.emptyList() :
                users.stream()
                        .map(User::getId)
                        .toList();
    }
}