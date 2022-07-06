package com.example.demo.config;

import com.example.demo.filter.JwtFilter;
import com.example.demo.model.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class JwtConfig extends WebSecurityConfigurerAdapter {
    private final JwtFilter filter;

    public static final int EXPIRATION_TIME = 15 * 60 * 1000;
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic();
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/api/v1/authenticate").permitAll();
        http.authorizeRequests().antMatchers("/api/v1/user/**").hasAuthority(Roles.USER.name());
        http.authorizeRequests().antMatchers("/api/v1/admin/**").hasAuthority(Roles.ADMIN.name());
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    }
}
