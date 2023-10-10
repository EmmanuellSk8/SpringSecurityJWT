package com.example.Security;

import com.example.Security.filters.JwtAuthenticationFilter;
import com.example.Security.filters.JwtAuthorizationFilter;
import com.example.Security.jwt.JwtUtils;
import com.example.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    JwtAuthorizationFilter authorizationFilter;


  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {

    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils);
    jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
    jwtAuthenticationFilter.setFilterProcessesUrl("/login");

      return httpSecurity
              .csrf(config -> config.disable())
              .authorizeHttpRequests(auth -> {
                  auth.requestMatchers("/hello").permitAll();
                  auth.anyRequest().authenticated();
              })
              .sessionManagement(session -> {
                  session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
              })
              .addFilter(jwtAuthenticationFilter)
              .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
              .build();
 }
/*@Bean
 UserDetailsService userDetailsService(){
     InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
     manager.createUser(User.withUsername("emmanuel")
             .password("1234")
             .roles()
             .build());

     return manager;
 }*/

 @Bean
    PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
 }
 @Bean
 AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder) throws Exception{
    return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder)
            .and() 
            .build();

 }

}
