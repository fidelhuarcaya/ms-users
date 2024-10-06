package org.copper.users.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.copper.users.common.RoleCode;
import org.copper.users.common.StatusCode;
import org.copper.users.dto.request.UserRequest;
import org.copper.users.dto.response.UserResponse;
import org.copper.users.entity.Role;
import org.copper.users.entity.Status;
import org.copper.users.entity.User;
import org.copper.users.entity.UserRole;
import org.copper.users.exception.RequestException;
import org.copper.users.mapper.UserMapper;
import org.copper.users.repository.RoleRepository;
import org.copper.users.repository.StatusRepository;
import org.copper.users.repository.UserRepository;
import org.copper.users.repository.UserRoleRepository;
import org.copper.users.service.status.StatusService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StatusRepository statusRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        userRequest.setPassword(passwordEncoder.encode(userRequest.getName().toLowerCase()));

        Status status = statusRepository.findByCode(StatusCode.ACTIVE).orElseThrow(()->new RequestException("No existe el estado."));
        User user = userMapper.toEntity(userRequest);
        user.setStatus(status);

        List<UserRole> userRoleList = new ArrayList<>();
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        Role role = roleRepository.findByCode(RoleCode.USER).orElseThrow(() -> new RuntimeException("El role no existe"));
        userRole.setRole(role);
        userRoleList.add(userRole);
        user.setUserRoles(userRoleList);
        user = userRepository.save(user);
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
