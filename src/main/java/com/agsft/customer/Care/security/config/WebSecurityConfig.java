package com.agsft.customer.Care.security.config;

import com.agsft.customer.Care.enums.RoleContants;
import com.agsft.customer.Care.security.jwt.JwtAuthenticationEntryPoint;
import com.agsft.customer.Care.security.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtAuthenticationEntryPoint authenticationEntryPoint;

    private static final String[] AUTH_WHITELIST = { "/v2/api-docs", "/swagger-resources", "/swagger-resources/**",
            "/configuration/ui", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/v3/api-docs/**",
            "/swagger-ui/**" ,"/js/**", "/css/**"};

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(AUTH_WHITELIST);

        web.ignoring().antMatchers("/swagger-ui.html", "/user/login/**", "/user/registration","/user/pdfProtected");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers("/admin/**").hasAuthority(RoleContants.SUPER_ADMIN.getValue())
                .antMatchers("/user/**").hasAnyAuthority(RoleContants.STAFF_ADMIN.getValue())
                .and().exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtFilter,
                UsernamePasswordAuthenticationFilter.class);
    }

}
