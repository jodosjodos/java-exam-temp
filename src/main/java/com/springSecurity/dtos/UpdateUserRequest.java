package com.springSecurity.dtos;

import lombok.Data;

@Data

public class UpdateUserRequest {

    private  String phoneNumber;
    private   String fullName;
    private  ContactInfo contactInfo;
}
