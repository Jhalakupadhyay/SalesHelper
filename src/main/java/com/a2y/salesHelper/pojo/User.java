package com.a2y.salesHelper.pojo;

import com.a2y.salesHelper.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Long tenantId;

    private String password;

    private Boolean isReset;

    private Role role; // Default role
}
