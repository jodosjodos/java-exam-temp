package com.springSecurity.services.impl;


import com.springSecurity.entities.UserData;
import com.springSecurity.errors.exception.ApiRequestException;
import com.springSecurity.services.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JWTServiceImpl implements JWTService {

    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);

    }

    @Override
    public String generateToken(UserDetails userDetails) {
        UserData user = (UserData) userDetails;
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        return generateRefreshToken(claims, userDetails);
    }

    @Override
    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 604800000))
                .signWith(getSigninkey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extralAllClaims(token);
        return claimsResolvers.apply(claims);
    }


    private Claims extralAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSigninkey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new ApiRequestException(" please provide valid token " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (JwtException e) {
            throw new ApiRequestException("token parsing failed  " + e.getMessage(), HttpStatus.UNAUTHORIZED);

        }


    }


    private Key getSigninkey() {
        byte[] key = Decoders.BASE64.decode("3D41112DCF3F434B58C192E379DF13D41112DCF3F434B58C192E379DF1");
        return Keys.hmacShaKeyFor(key);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);

        return (Objects.equals(username, userDetails.getUsername()) && isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        final Date expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate.after(new Date());
    }

}
