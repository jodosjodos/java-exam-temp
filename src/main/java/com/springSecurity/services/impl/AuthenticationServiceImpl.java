package com.springSecurity.services.impl;


import com.springSecurity.entities.UserData;
import com.springSecurity.dtos.JWtAuthenticationResponse;
import com.springSecurity.dtos.RefreshTokenRequest;
import com.springSecurity.dtos.SignInRequest;
import com.springSecurity.dtos.SignUpRequest;
import com.springSecurity.errors.exception.ApiRequestException;
import com.springSecurity.repository.UserRepository;
import com.springSecurity.services.AuthenticationService;
import com.springSecurity.services.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    //     delete  UserData
    @Override
    public UserData signUp(SignUpRequest signUpRequest) {
        UserData userData = new UserData();
        userData.setFullName(signUpRequest.getFullName());
        userData.setEmail(signUpRequest.getEmail());
        userData.setPhoneNumber(signUpRequest.getPhoneNumber());
        userData.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        userData.setRole(signUpRequest.getRole());
        userData.setContactInfo(signUpRequest.getContactInfo());
        log.info(signUpRequest.toString());
        boolean userExists = userRepository.findByEmail(signUpRequest.getEmail()).isPresent();
        if (userExists) {
            throw new ApiRequestException(" UserData already exists", HttpStatus.CONFLICT);
//
        } else {
            return userRepository.save(userData);
        }

    }

    //     sign in UserData
    @Override
    public JWtAuthenticationResponse signin(SignInRequest signinRequest) {


        try {
//            authentication manager
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
//            get logged UserData
            var userData = (UserData) authentication.getPrincipal();
//            generate token
            var jwt = jwtService.generateToken(userData);
//            generate refresh token
            var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), userData);
//            response
            return JWtAuthenticationResponse.builder()
                    .refreshToken(refreshToken)
                    .token(jwt)
                    .user(userData)
                    .build();
        } catch (BadCredentialsException ex) {
            throw new ApiRequestException(" invalid credentials", HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            throw new ApiRequestException(ex.getMessage(), HttpStatus.UNAUTHORIZED);

        }
    }

    //     get refresh token
    public JWtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userEmail = jwtService.extractUserName(refreshTokenRequest.getRefreshToken());
        var userData = userRepository.findByEmail(userEmail).orElseThrow(() -> new ApiRequestException(" UserData not found", HttpStatus.NOT_FOUND));

        if (jwtService.isTokenValid(refreshTokenRequest.getRefreshToken(), userData)) {
            var jwt = jwtService.generateToken(userData);

            return JWtAuthenticationResponse.builder()
                    .refreshToken(refreshTokenRequest.getRefreshToken())
                    .token(jwt)

                    .build();
        }
        return null;
    }
}
