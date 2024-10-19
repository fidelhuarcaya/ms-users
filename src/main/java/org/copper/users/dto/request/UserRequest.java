package org.copper.users.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UserRequest {

    private Long id;
    private String dni;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    private String lastName;

    private String secondLastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    private String password;

    private Integer roleId;
    private Integer statusId;
}

