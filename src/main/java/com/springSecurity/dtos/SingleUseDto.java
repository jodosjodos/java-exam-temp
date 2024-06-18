package com.springSecurity.dtos;

import com.springSecurity.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleUseDto {
    private Integer id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;
    private Role role;
    private ContactInfo contactInfo;
}
