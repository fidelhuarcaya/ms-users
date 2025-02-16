package org.copper.users.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.copper.users.dto.request.UserRequest;
import org.copper.users.dto.response.UserResponse;
import org.copper.users.entity.Role;
import org.copper.users.entity.User;
import org.copper.users.entity.UserRole;
import org.copper.users.exception.RequestException;
import org.copper.users.mapper.UserMapper;
import org.copper.users.repository.RoleRepository;
import org.copper.users.repository.StatusRepository;
import org.copper.users.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        userRequest.setPassword(passwordEncoder.encode(userRequest.getDni().toLowerCase()));
        User user = userMapper.toEntity(userRequest);

        List<UserRole> userRoleList = new ArrayList<>();
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        Role role = roleRepository.findById(userRequest.getRoleId())
                .orElseThrow(() -> new RuntimeException("El role no existe"));
        userRole.setRole(role);
        userRoleList.add(userRole);
        user.setUserRoles(userRoleList);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RequestException("Usuario no existe"));
        List<UserRole> userRoles = new ArrayList<>(user.getUserRoles());  // Use a mutable list
        String password = user.getPassword();
        user = userMapper.toEntity(userRequest);
        user.setId(id);
        user.setUserRoles(userRoles);
        user.setPassword(password);

        Role newRole = roleRepository.findById(userRequest.getRoleId())
                .orElseThrow(() -> new RequestException("Rol no existe"));
        boolean roleExists = userRoles.stream()
                .anyMatch(ur -> ur.getRole().getId().equals(newRole.getId()));
        if (!roleExists) {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(newRole);
            userRoles.add(userRole);  // Add to mutable list
        }

        user.getUserRoles().removeIf(ur -> !userRequest.getRoleId().equals(ur.getRole().getId()));

        return userMapper.toDto(userRepository.save(user));
    }


    @Override
    public UserResponse getUserById(Long id) {
        return userMapper
                .toDto(userRepository.findById(id).orElseThrow(() -> new RequestException("El usuario no existe")));
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        return userMapper
                .toDto(userRepository.findByEmail(email).orElseThrow(() -> new RequestException("El usuario no existe")));
    }
}
