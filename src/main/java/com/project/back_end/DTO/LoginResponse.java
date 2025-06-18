package com.project.back_end.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * JWTログイン成功時のレスポンスDTO。
 */
@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String role;
}
