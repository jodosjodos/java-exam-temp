package com.springSecurity.services.impl;

import com.springSecurity.entities.UserData;
import com.springSecurity.errors.exception.ApiRequestException;
import com.springSecurity.dtos.*;
import com.springSecurity.repository.UserRepository;
import com.springSecurity.services.JWTService;
import com.springSecurity.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @Override
    public SingleUseDto getUser() {
        UserData userData = getUserData();
        return SingleUseDto.builder()
                .id(userData.getId()).
                fullName(userData.getFullName()).
                email(userData.getEmail()).
                phoneNumber(userData.getPhoneNumber()).
                password(userData.getPassword()).
                role(userData.getRole()).
                contactInfo(userData.getContactInfo())
                .build();
    }

    @Override
    public UserData updateUser(UpdateUserRequest updateUserRequest) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        var token = securityContext.getAuthentication().getCredentials().toString();
        var userEmail = jwtService.extractUserName(token);
        UserData user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiRequestException(" user not found",
                        HttpStatus.NOT_FOUND));
        if (updateUserRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateUserRequest.getPhoneNumber());
        }
        if (updateUserRequest.getFullName() != null) {
            user.setFullName(updateUserRequest.getFullName());
        }
        if (updateUserRequest.getContactInfo() != null) {
            user.setContactInfo(updateUserRequest.getContactInfo());
        }

        log.info(" update user have been called : " + user);
        userRepository.save(user);
        return user;
    }

    @Override
    public Optional<UserData> deleteUSer() throws UsernameNotFoundException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        var token = securityContext.getAuthentication().getCredentials().toString();
        var userEmail = jwtService.extractUserName(token);
        var user = userRepository.findByEmail(userEmail);
        userRepository.deleteByEmail(userEmail);
        log.warn("User account has been successfully deleted.");
        return user;
    }

    @Override
    public UpdatePasswordResponse updatePassword(ResetPasswordRequest resetPasswordRequest) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        var token = securityContext.getAuthentication().getCredentials().toString();
        var userEmail = jwtService.extractUserName(token);
        UserData user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiRequestException(" user not found",
                        HttpStatus.NOT_FOUND));
        if (passwordEncoder.matches(resetPasswordRequest.getOldPassword(),
                user.getPassword())) {
            String encodedPassword = passwordEncoder.encode(resetPasswordRequest.getNewPassword());
            log.info(encodedPassword);
            user.setPassword(encodedPassword);
            userRepository.save(user);
            log.info(" user password have been updated successfully");
            return UpdatePasswordResponse.builder().email(userEmail)
                    .msg(" user with this email have updated his password successfully").build();
        } else {
            throw new ApiRequestException(" please make sure that old password match with that " +
                    "one you have saved in db",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private UserData getUserData() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                return (UserData) userDetails;
            } else {
                throw new ApiRequestException("something went wrong",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new ApiRequestException("User not authenticated",
                    HttpStatus.UNAUTHORIZED);
        }
    }
}
