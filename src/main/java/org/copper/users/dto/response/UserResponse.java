package org.copper.users.dto.response;

import lombok.Data;
import org.copper.users.entity.Role;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private List<Role> roles;
    private LocalDateTime createdDate;
}

