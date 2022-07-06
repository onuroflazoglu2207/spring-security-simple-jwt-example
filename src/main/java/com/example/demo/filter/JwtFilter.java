package com.example.demo.filter;

import com.example.demo.config.JwtConfig;
import com.example.demo.service.DemoServiceImpl;
import com.example.demo.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@Configuration
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenUtil util;
    private final DemoServiceImpl service;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String token = request.getHeader(JwtConfig.AUTHORIZATION);
            if (token != null && token.startsWith(JwtConfig.BEARER)) {
                token = token.substring(JwtConfig.BEARER.length());
                String identity = util.getSubject(token);
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails details = service.getUserDetails(identity);
                    if (util.validateToken(token, details)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken
                                (details.getUsername(), details.getPassword(), details.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else logger.info("JWT Token token not validate!");
                }
            }
        } catch (IllegalArgumentException e) {
            logger.info("JWT Token unacceptable token!");
        } catch (ExpiredJwtException e) {
            logger.info("JWT Token time is up!");
        }
        chain.doFilter(request, response);
    }
}