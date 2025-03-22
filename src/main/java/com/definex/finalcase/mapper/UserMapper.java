package com.definex.finalcase.mapper;

import com.definex.finalcase.domain.entity.User;
import com.definex.finalcase.domain.enums.Role;
import com.definex.finalcase.domain.request.UserRequest;
import com.definex.finalcase.domain.response.UserResponse;
import com.definex.finalcase.mapper.helper.PasswordEncodeMapper;
import com.definex.finalcase.security.UserPrincipal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {PasswordEncodeMapper.class})
public interface UserMapper {

    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    User toEntity(UserRequest request);

    UserResponse toResponse(User user);

    @Mapping(target = "authorities", source = "role", qualifiedByName = "mapAuthorities")
    @Mapping(target = "userId", source = "id")
    UserPrincipal convertToUserPrincipal(User user);

    @Named("mapAuthorities")
    default List<GrantedAuthority> mapAuthorities(Role role) {
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }
}
