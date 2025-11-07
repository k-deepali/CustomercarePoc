package com.agsft.customer.Care.security.jwt;

import com.agsft.customer.Care.model.Role;
import com.agsft.customer.Care.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class JwtUserFactory {

    public static JwtUser create(User user) {
        return new JwtUser(user.getId(), user.getEmail(), user.getFirstName().concat(" ").concat(user.getLastName()),
                mapToGrantedAuthorities(new ArrayList<>(user.getRoles())));

    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(ArrayList<Role> authorities) {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        for (Role role : authorities) {
            //set role authority
            authorityList.add(new SimpleGrantedAuthority(role.getName()));

        }
        return authorityList;
    }
}
