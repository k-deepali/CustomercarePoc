package com.agsft.customer.Care.util;
import com.agsft.customer.Care.enums.RoleContants;
import com.agsft.customer.Care.model.User;
import com.agsft.customer.Care.repository.UserRepository;
import com.agsft.customer.Care.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
@Component
public class UserUtil {

    @Value("${jwt.header}")
    private String tokenHeader;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRepository userRepository;
    public Optional<User> getLoginUserFromToken(HttpServletRequest request) {

        String authToken = request.getHeader(tokenHeader);
        String[] token=authToken.split("\\s");
        String emailId = jwtTokenUtil.getUsernameFromToken(token[1]);
        return userRepository.findByEmail(emailId);
    }

    public boolean isUserSuperAdmin(User user) {
        if (user != null && user.getRoles() != null && !user.getRoles().isEmpty() && user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleContants.SUPER_ADMIN.getValue()))) {
            return true;
        }
        return false;
    }

    public boolean isUserStaffAdmin(User user) {
        if (user != null && user.getRoles() != null && !user.getRoles().isEmpty() && user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleContants.STAFF_ADMIN.getValue()))) {
            return true;
        }
        return false;
    }
}