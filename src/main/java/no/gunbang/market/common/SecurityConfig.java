package no.gunbang.market.common;

import lombok.AllArgsConstructor;
import no.gunbang.market.common.exception.CustomAccessDeniedHandler;
import no.gunbang.market.common.exception.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        //로그인시 세션 항상 생성
        http.sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
        );

        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/login", "/markets/main", "/auctions/main", "/markets/populars", "/auctions/populars").permitAll()
            .requestMatchers("/auth/logout", "/markets/**", "/auctions/**", "/user/**").authenticated()
            .anyRequest().authenticated()
        );

        http.securityContext(securityContext ->
            securityContext.requireExplicitSave(false)
        );

        http.exceptionHandling(exception ->
            exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}