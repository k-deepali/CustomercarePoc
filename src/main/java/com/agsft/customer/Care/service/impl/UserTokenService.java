package com.agsft.customer.Care.service.impl;

import com.agsft.customer.Care.model.UserToken;
import com.agsft.customer.Care.repository.UserTokenRepository;
import com.agsft.customer.Care.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class UserTokenService {

    @Autowired
    UserTokenRepository userTokenRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    public Optional<UserToken> getUserToken(HttpServletRequest request) {
        String authenticationToken = jwtTokenUtil.getUserToken(request);
        String token = authenticationToken.substring(7);
        Optional<UserToken> userToken = userTokenRepository.findByToken(token);
        return userToken;
    }

    public synchronized void deleteToken(UserToken userToken) {
        if (userToken != null) {
            userTokenRepository.delete(userToken);
        }
    }
}
