package com.springSecurity.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springSecurity.entities.UserData;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JWtAuthenticationResponse {
    @JsonProperty("token")
    private  String token;
    @JsonProperty("refresh-token")
    private  String refreshToken;
    @JsonProperty("userDetails")
    private UserData user;
}
