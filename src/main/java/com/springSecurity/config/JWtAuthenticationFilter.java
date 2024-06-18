package com.springSecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springSecurity.errors.JWtAuthenticationResponse;
import com.springSecurity.errors.exception.ApiRequestException;
import com.springSecurity.services.JWTService;
import com.springSecurity.services.impl.UserServiceSecurityImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserServiceSecurityImpl userServiceSecurity;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null) {
            filterChain.doFilter(request,
                    response);
            return;
        }
        if (!authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request,
                    response);
            return;
        }
//             check if token have been provided if not then return error require him to provide
//             bearer token
        try {
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractUserName(jwt);


            if (userEmail != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userServiceSecurity.loadUserByUsername(userEmail);
//  if user present add him or her to context so that I can access him to all my apis
                if (jwtService.isTokenValid(jwt,
                        userDetails)) {
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken token =
                            new UsernamePasswordAuthenticationToken(userDetails,
                            jwt,
                            userDetails.getAuthorities());
                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(token);
                    SecurityContextHolder.setContext(securityContext);

                }
            }


            filterChain.doFilter(request,
                    response);
        } catch (StringIndexOutOfBoundsException e) {
            JWtAuthenticationResponse jWtAuthenticationResponse =
                    new com.springSecurity.errors.JWtAuthenticationResponse(" please provide " +
                            "token in header",
                    null,
                    null,
                    HttpServletResponse.SC_UNAUTHORIZED);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ObjectMapper mapper = new ObjectMapper();
            response.setContentType("application/json");
            response.getWriter().write(mapper.writeValueAsString(jWtAuthenticationResponse));
        } catch (ApiRequestException e) {

//                  when user not found throw error and don't continue to filter return with
//                  below response customized below
            JWtAuthenticationResponse jWtAuthenticationResponse = new JWtAuthenticationResponse(
                    "user  with this email : not found  please provide token with email that " +
                            "found or you can try to create new account",
                    e.getMessage(),
                    e.getStatus(),
                    HttpServletResponse.SC_UNAUTHORIZED);
            log.error("User not found");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ObjectMapper mapper = new ObjectMapper();
            response.setContentType("application/json");
            response.getWriter().write(mapper.writeValueAsString(jWtAuthenticationResponse));
        }


    }
}
