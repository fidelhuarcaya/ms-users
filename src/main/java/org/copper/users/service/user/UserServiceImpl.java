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
    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RequestException("Usuario no existe"));
        List<UserRole> userRoles = user.getUserRoles();
        String password = user.getPassword();
        user = userMapper.toEntity(userRequest);
        user.setId(id);
        user.setUserRoles(userRoles);
        user.setPassword(password);

        Role newRole = roleRepository.findById(userRequest.getRoleId())
                .orElseThrow(() -> new RequestException("Rol no existe"));

        // Verificar si el usuario ya tiene el rol
        boolean roleExists = userRoles.stream()
                .anyMatch(ur -> ur.getRole().getId().equals(newRole.getId()));

        if (!roleExists) {
            // Si el rol no existe, agregarlo
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(newRole);
            user.getUserRoles().add(userRole);
        }

        // Eliminar roles que ya no están en la solicitud
        user.getUserRoles().removeIf(ur -> !ur.getRole().getId().equals(newRole.getId()));

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponse getUserById(Long id) {
        return userMapper
                .toDto(userRepository.findById(id).orElse(null));
    }
}
