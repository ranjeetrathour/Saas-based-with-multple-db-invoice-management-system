package com.moptra.invoice.user.dto.req;

import com.moptra.invoice.user.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    private String name;
    private String email;
    private String password;
    private UserRole role;
}