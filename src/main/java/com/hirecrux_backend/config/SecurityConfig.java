package com.hirecrux_backend.config;

import com.hirecrux_backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())  //Cross-Site Request Forgery isn't needed for our JWT-based API.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/register", "/api/users/login").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/job/create")
                        .hasRole("HR")

                        .requestMatchers(HttpMethod.PUT, "/api/job/update/*")
                        .hasRole("HR")

                        .requestMatchers(HttpMethod.PUT, "/api/job/close/*")
                        .hasRole("HR")

                        .requestMatchers(HttpMethod.GET, "/api/candidate-profile/getCandidateProfile")
                        .hasRole("HR")

                        .requestMatchers(HttpMethod.GET, "/api/application/*")
                        .hasRole("HR")

                        .requestMatchers(HttpMethod.PUT, "/api/application/update/*")
                        .hasRole("HR")

                        .requestMatchers(HttpMethod.GET, "/api/application/job/*")
                        .hasRole("HR")

                        .requestMatchers(HttpMethod.POST, "/api/notification/create-notification")
                        .hasRole("HR")

                        .requestMatchers(HttpMethod.POST, "/api/interview/schedule-interview")
                        .hasRole("HR")


                        // Candidate APIs
                        .requestMatchers(HttpMethod.POST, "/api/application/createApplication")
                        .hasRole("CANDIDATE")

                        .requestMatchers(HttpMethod.PUT, "/api/candidate-profile/updateProfile")
                        .hasRole("CANDIDATE")

                        .requestMatchers(HttpMethod.POST, "/api/candidate-profile/create")
                        .hasRole("CANDIDATE")


                        // Shared APIs (HR + Candidate)
                        .requestMatchers(HttpMethod.GET, "/api/job/all")
                        .authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/job/*")
                        .authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/job/searchJob")
                        .authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/candidate-profile/myProfile")
                        .authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/notification/myNotification")
                        .authenticated()

                        .requestMatchers(HttpMethod.PUT, "/api/notification/markAsRead/*")
                        .authenticated()

                        // Any remaining request
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}