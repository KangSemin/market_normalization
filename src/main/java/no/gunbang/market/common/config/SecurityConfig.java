package no.gunbang.market.common.config;

import static org.springframework.security.config.Customizer.withDefaults;

import lombok.AllArgsConstructor;
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

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.securityMatcher("/**")
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/markets/main", "/auctions/main",
                    "/markets/populars", "/auctions/populars", "/actuator/health").permitAll()
                .requestMatchers("/auth/logout", "/markets/**", "/auctions/**", "/user/**")
                .authenticated()
                .anyRequest().authenticated()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(withDefaults())
            .securityContext(securityContext ->
                securityContext.requireExplicitSave(false)
            )
            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(authenticationEntryPoint)
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
