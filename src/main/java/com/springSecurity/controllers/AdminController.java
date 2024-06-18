package com.springSecurity.controllers;

import com.springSecurity.entities.UserData;
import com.springSecurity.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    //     get all user
    @Operation(
            summary = "get all users by admin token",
            description = "protected operation for  get all users by providing    admin token , if it isn't admin token this can success",
            responses = {
                    @ApiResponse(

                            description = " success",
                            responseCode = "200",
                            useReturnTypeSchema = true
                    ),
                    @ApiResponse(
                            description = " invalid token or Unauthorized",
                            responseCode = "403 or 401"


                    )
                    ,
                    @ApiResponse(
                            description = "user not found",
                            responseCode = "404"


                    )

            }, method = "get all users"


    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/users")
    public ResponseEntity<List<UserData>> getAllUsers() {
        return ResponseEntity.ok().body(adminService.getAllUsers());
    }
    @DeleteMapping("/users/{userEmail}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteUserById(@PathVariable String userEmail) {
        adminService.deleteUserById(userEmail);
        return ResponseEntity.noContent().build();
    }
}
