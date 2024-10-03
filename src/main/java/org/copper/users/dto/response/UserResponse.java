package org.copper.users.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private StatusResponse status;

    @JsonFormat(pattern = "dd/MM/yyy hh:mm:ss")
    private LocalDateTime createdDate;
}

