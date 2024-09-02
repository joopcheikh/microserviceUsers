package com.getusers.getusers.filter;

import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class CustomAuthFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            try {
                filterChain.doFilter(servletRequest,servletResponse);
            } catch (java.io.IOException | ServletException e) {
                e.printStackTrace();
            }
    }
}