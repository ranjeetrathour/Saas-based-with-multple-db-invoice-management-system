package com.moptra.invoice.user.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
    private String email;
    private String tenantId;
    private String message;
}