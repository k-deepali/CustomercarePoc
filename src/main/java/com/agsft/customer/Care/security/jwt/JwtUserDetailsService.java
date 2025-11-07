package com.agsft.customer.Care.security.jwt;

import com.agsft.customer.Care.model.User;
import com.agsft.customer.Care.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent())
            throw new UsernameNotFoundException(String.format("No user exists with email : %s", email));
        else
            return JwtUserFactory.create(user.get());
    }
}
