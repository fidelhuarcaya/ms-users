package org.copper.users.service.user;

import org.copper.users.dto.request.UserRequest;
import org.copper.users.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long id, UserRequest userRequest);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);
}
