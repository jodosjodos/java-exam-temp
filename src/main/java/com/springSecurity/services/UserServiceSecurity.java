package com.springSecurity.services;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserServiceSecurity {
    UserDetailsService userDetailsService();

}
