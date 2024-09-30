package org.copper.users.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.copper.users.common.RoleCode;
import org.copper.users.dto.request.UserRequest;
import org.copper.users.dto.response.UserResponse;
import org.copper.users.entity.Role;
import org.copper.users.entity.User;
import org.copper.users.entity.UserRole;
import org.copper.users.exception.RequestException;
import org.copper.users.mapper.UserMapper;
import org.copper.users.repository.RoleRepository;
import org.copper.users.repository.UserRepository;
import org.copper.users.repository.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;


    @Override
    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);
        user = userRepository.save(user);
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        Role role = roleRepository.findByCode(RoleCode.USER).orElseThrow(() -> new RuntimeException("El role no existe"));
        userRole.setRole(role);

        userRoleRepository.save(userRole);
        return userMapper.toDto(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        if (!userRepository.existsById(id)) {
            throw new RequestException("Usuario no existe");
        }
        User user = userMapper.toEntity(userRequest);
        user.setId(id);

        List<UserRole> userRoles = userRequest.getRoleIds().stream().map(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            Role role = new Role();
            role.setId(roleId);
            userRole.setRole(role);
            return userRole;
        }).toList();
        user.setUserRoles(userRoles);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponse getUserById(Long id) {
        return userMapper.toDto(userRepository.findById(id).orElse(null));
    }
}
