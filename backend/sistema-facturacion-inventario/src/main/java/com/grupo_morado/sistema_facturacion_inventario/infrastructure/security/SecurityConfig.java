package com.grupo_morado.sistema_facturacion_inventario.infrastructure.security;

import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository.UserDAO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDAO userDAO;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(config -> config
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Permitir conexión a WebSocket SockJS
                        .requestMatchers("/ws/**").permitAll()
                        // Endpoints que requieren autenticación aunque estén bajo /api/auth/**
                        // (las reglas más específicas deben ir antes de la wildcard)
                        .requestMatchers("/api/auth/update-password").authenticated()
                        .requestMatchers("/api/auth/change-password").authenticated()
                        .requestMatchers("/api/auth/logout").authenticated()
                        // El resto de /api/auth/** (login, register, reset-password) es público
                        .requestMatchers("/api/auth/**").permitAll()
                        // Reglas del módulo de Gestión de Mesas
                        .requestMatchers(HttpMethod.POST, "/api/mesas/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/mesas/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/mesas/**").authenticated()
                        // Reglas del módulo de Gestión de Menús
                        .requestMatchers(HttpMethod.POST, "/api/menus/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/menus/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/menus/**").authenticated()
                        // Reglas del módulo de Gestión de Platos
                        .requestMatchers(HttpMethod.POST, "/api/platos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/platos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/platos/**").authenticated()
                        // Reglas del módulo de Órdenes
                        .requestMatchers(HttpMethod.POST, "/api/ordenes").hasRole("MESERO")
                        .requestMatchers(HttpMethod.PUT, "/api/ordenes/*").hasRole("MESERO")
                        .requestMatchers(HttpMethod.PUT, "/api/ordenes/*/estado").hasRole("CHEF")
                        .requestMatchers(HttpMethod.PUT, "/api/ordenes/**").hasAnyRole("ADMINISTRADOR", "MESERO")
                        .requestMatchers(HttpMethod.GET, "/api/ordenes/**").hasAnyRole("ADMINISTRADOR", "MESERO", "CHEF")
                        // Reglas del módulo de Facturación
                        .requestMatchers(HttpMethod.PUT, "/api/facturacion/**").hasRole("CAJERO")
                        // Reglas del módulo de Informes
                        .requestMatchers(HttpMethod.GET, "/api/informes/ventas/diario").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/informes/caja/cierre").hasRole("CAJERO")
                        // Reglas del módulo de Gestión de Usuarios (SFR-008)
                        .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated()
                )
                // Registrar el filtro JWT antes del filtro estándar de usuario/contraseña
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        return request -> configuration;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userDAO.findByEmail(email)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "El usuario con correo electronico " + email + " no fue encontrado."));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}