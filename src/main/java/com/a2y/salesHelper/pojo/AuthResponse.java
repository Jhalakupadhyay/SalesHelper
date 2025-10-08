package com.a2y.salesHelper.pojo;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private Long tenantId;
    private String message;
    private Long userId;
    private String email;
    private String role;
}
