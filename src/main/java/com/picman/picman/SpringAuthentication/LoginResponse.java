package com.picman.picman.SpringAuthentication;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginResponse {
    private String token;
    private String expiration;
}