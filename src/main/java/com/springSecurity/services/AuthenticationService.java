package com.springSecurity.services;


import com.springSecurity.entities.UserData;
import com.springSecurity.dtos.JWtAuthenticationResponse;
import com.springSecurity.dtos.RefreshTokenRequest;
import com.springSecurity.dtos.SignInRequest;
import com.springSecurity.dtos.SignUpRequest;

public interface AuthenticationService {
    UserData signUp(SignUpRequest signUpRequest);
    JWtAuthenticationResponse signin(SignInRequest signInRequest);
    public  JWtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
