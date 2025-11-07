package com.agsft.customer.Care.security.jwt;

import com.agsft.customer.Care.model.User;
import com.agsft.customer.Care.model.UserToken;
import com.agsft.customer.Care.repository.UserTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    public String secretKey;

    @Value("${jwt.tokenValidity}")
    public Long jwtTokenValidity;

    @Autowired
    private UserTokenRepository tokenRepository;

    @Value("${jwt.header}")
    String tokenHeader;

    public Claims getClaimFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

    }

    public Boolean isTokenExpired(String token) {

        Claims claims = getClaimFromToken(token);
        Optional<UserToken> authToken = tokenRepository.findByToken(token);
        if (authToken.isPresent()) {
            if (authToken.get().getExpirationTime().getTime() < new Date().getTime()) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    public String generateToken(User user) {
        String authorities = new UsernamePasswordAuthenticationToken(user.getEmail(), null, user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList())).getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        claims.put("userName", user.getEmail());
        claims.put("auth", authorities);
        claims.put("password", user.getPassword());

        return Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidity * 1000))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        if(!username.equals(userDetails.getUsername())){
            return false;
        }else{
            return true;
        }
    }

    public String  getUsernameFromToken(String token) {

        String username;
        try {
            final Claims claims = getClaimFromToken(token);
            username = (String) claims.get("userName");
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public String getUserToken(HttpServletRequest request) {
        return request.getHeader(tokenHeader);
    }
}
