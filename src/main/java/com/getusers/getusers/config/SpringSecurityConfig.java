package com.getusers.getusers.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import javax.sql.DataSource;

@EnableMethodSecurity 
@Configuration
public class SpringSecurityConfig {

    @Autowired
    private DataSource ds;

    @Autowired
    public void configureAMBuilder (AuthenticationManagerBuilder auth) throws Exception{
        auth.jdbcAuthentication().dataSource(ds)
        .authoritiesByUsernameQuery("select email, role from users where email=?")
        .usersByUsernameQuery("select email, password, 1 from users where email=?");

    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                        .anyRequest().hasAuthority("ADMIN"))
                .httpBasic(basic -> basic.realmName("My Realm"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
