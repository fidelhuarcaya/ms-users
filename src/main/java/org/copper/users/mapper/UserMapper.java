package org.copper.users.mapper;

import org.copper.users.dto.request.UserRequest;
import org.copper.users.dto.response.UserResponse;
import org.copper.users.entity.Role;
import org.copper.users.entity.User;
import org.copper.users.entity.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequest userRequest);

    @Mapping(target = "roles", source = "userRoles")
    @Mapping(target = "status", source = "status")
    UserResponse toDto(User user);

    List<UserResponse> toDtos(List<User> users);

    default List<Role> mapRoles(List<UserRole> userRoles) {
        return userRoles.stream()
                .map(UserRole::getRole) // Extrae el Role de UserRole
                .toList();
    }
}

