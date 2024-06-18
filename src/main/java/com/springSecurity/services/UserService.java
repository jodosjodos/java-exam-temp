package com.springSecurity.services;


import com.springSecurity.entities.UserData;
import com.springSecurity.dtos.ResetPasswordRequest;
import com.springSecurity.dtos.SingleUseDto;
import com.springSecurity.dtos.UpdatePasswordResponse;
import com.springSecurity.dtos.UpdateUserRequest;

import java.util.Optional;

public interface UserService {
    SingleUseDto getUser();

    UserData updateUser(UpdateUserRequest updateUserRequest);

    Optional<UserData> deleteUSer();

    UpdatePasswordResponse updatePassword(ResetPasswordRequest resetPasswordRequest);
}
