package com.springSecurity.services.impl;

import com.springSecurity.entities.UserData;
import com.springSecurity.errors.exception.ApiRequestException;
import com.springSecurity.repository.UserRepository;
import com.springSecurity.services.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;

    @Override
    public List<UserData> getAllUsers() {
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            UserData user = (UserData) securityContext.getAuthentication().getPrincipal();
//            //TODO:logical solution
            if (!user.getRole().toString().equals("ADMIN")) {
                // Check if the user's role is not "ADMIN"
                throw new ApiRequestException("Unauthorized role. You must be an ADMIN to access " +
                        "this endpoint",
                        HttpStatus.UNAUTHORIZED);
            }
            return userRepository.findAll();
        } catch (Exception ex) {
            throw new ApiRequestException(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteUserById(String userEmail) {
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            UserData user = (UserData) securityContext.getAuthentication().getPrincipal();
//            //TODO:logical solution
            if (!user.getRole().toString().equals("ADMIN")) {
                // Check if the user's role is not "ADMIN"
                throw new ApiRequestException("Unauthorized role. You must be an ADMIN to access " +
                        "this endpoint",
                        HttpStatus.UNAUTHORIZED);
            }
            userRepository.deleteByEmail(userEmail);
        } catch (Exception ex) {
            throw new ApiRequestException(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
