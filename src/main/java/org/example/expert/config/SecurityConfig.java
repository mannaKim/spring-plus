package org.example.expert.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.common.security.JwtAuthenticationFilter;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final String profile = System.getProperty("spring.profiles.active");

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않도록 설정
                .addFilterBefore(jwtAuthenticationFilter, SecurityContextHolderAwareRequestFilter.class)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/auth/**").permitAll();           // 회원가입/로그인은 인증 없이 접근 가능
                    auth.requestMatchers("/health").permitAll();            // health check API는 인증 없이 접근 가능
                    //auth.requestMatchers("/actuator/health").permitAll();   // health check API는 인증 없이 접근 가능
                    auth.requestMatchers("/admin").hasAuthority(UserRole.Authority.ADMIN);  // 관리자 권한 필요
                    if (isDevProfile()) {
                        auth.requestMatchers("/h2-console/**").permitAll(); // 개발 환경에서 H2 콘솔 접근 허용
                    }
                    auth.anyRequest().authenticated();  // 위의 예외를 제외한 모든 요청은 인증 필요
                })
                .formLogin(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable);

        if (isDevProfile()) {
            http.headers(headers ->
                    headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)); // H2 Console 사용을 위한 FrameOptions 비활성화
        }

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private boolean isDevProfile() {
        return "dev".equals(profile);
    }
}
