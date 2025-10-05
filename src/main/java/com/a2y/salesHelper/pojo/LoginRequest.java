package com.a2y.salesHelper.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginRequest {
    private String email;
    private String password;
    private String token; // optional, e.g., for re-auth
    // getters and setters
}

