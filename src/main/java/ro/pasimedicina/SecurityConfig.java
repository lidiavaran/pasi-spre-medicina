package ro.pasimedicina;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configurarea securitatii si a regulilor de acces pentru utilizatori
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defineste rutele protejate si regulile de redirectionare dupa autentificare
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/register").permitAll()
                
                .requestMatchers("/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                
                .requestMatchers("/mentor/**").hasAnyAuthority("MENTOR", "ROLE_MENTOR", "ADMIN", "ROLE_ADMIN")
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    var authorities = authentication.getAuthorities().stream()
                                        .map(a -> a.getAuthority())
                                        .toList();
                    
                    if (authorities.contains("ROLE_ADMIN") || authorities.contains("ADMIN")) {
                        response.sendRedirect("/admin/dashboard");
                    } else if (authorities.contains("ROLE_MENTOR") || authorities.contains("MENTOR")) {
                        response.sendRedirect("/mentor/dashboard");
                    } else {
                        response.sendRedirect("/home"); 
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    /**
     * Bean pentru criptarea parolelor folosind algoritmul bcrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}