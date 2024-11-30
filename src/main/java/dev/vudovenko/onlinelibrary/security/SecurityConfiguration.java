package dev.vudovenko.onlinelibrary.security;

import dev.vudovenko.onlinelibrary.security.jwt.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * Конфигурация безопасности для приложения.
 * Этот класс настраивает аутентификацию и авторизацию пользователей,
 * управление сессиями и настройки безопасности HTTP.
 * <p>
 * Отключается CSRF-защита и используется stateless-сессии для API.
 * Настраивается доступ к определённым URL в зависимости от ролей и методов запросов.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserDetailsService customUserDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtTokenFilter jwtTokenFilter;

    /**
     * Конфигурирует фильтры безопасности для HTTP-запросов.
     * Отключает форму входа и CSRF-защиту, устанавливает управление сессиями в режиме "STATELESS".
     * <p>
     * Настраивает разрешения доступа к определённым эндпоинтам на основе ролей и методов HTTP-запросов.
     * При возникновении ошибки при аутентификации пользователя,
     * например, с невалидным логином или паролем, заблокированным аккаунтом,
     * будет вызываться {@link CustomAuthenticationEntryPoint}.
     * <p>
     * При возникновении ошибки при доступе к ресурсу, для которого у пользователя нет необходимых прав,
     * будет вызываться {@link CustomAccessDeniedHandler}.
     * <p>
     * Стандартный {@link ControllerAdvice} не может обработать такие ошибки, потому что
     * исключения аутентификации происходят на этапе фильтрации запросов в Spring Security,
     * до того как запрос достигнет контроллера.
     * Поэтому для обработки этих исключений необходимо использовать {@link AuthenticationEntryPoint}.
     *
     * @param http объект для настройки фильтров безопасности HTTP.
     * @return конфигурация фильтров безопасности для HTTP-запросов.
     * @throws Exception если происходит ошибка при настройке фильтров безопасности.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(
                        authorizeHttpRequests ->
                                authorizeHttpRequests
                                        .requestMatchers(HttpMethod.POST, "/authors")
                                        .hasAnyAuthority("ADMIN")
                                        .requestMatchers(HttpMethod.GET, "/authors/**")
                                        .hasAnyAuthority("ADMIN", "USER")

                                        .requestMatchers(HttpMethod.POST, "/books")
                                        .hasAnyAuthority("ADMIN")
                                        .requestMatchers(HttpMethod.GET, "/books/**")
                                        .hasAnyAuthority("ADMIN", "USER")
                                        .requestMatchers(HttpMethod.DELETE, "/books/**")
                                        .hasAnyAuthority("ADMIN")
                                        .requestMatchers(HttpMethod.PUT, "/books/**")
                                        .hasAnyAuthority("ADMIN")

                                        .requestMatchers(HttpMethod.POST, "/users")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.POST, "/users/auth")
                                        .permitAll()
                                        .anyRequest().authenticated()
                )
                .exceptionHandling(
                        exception ->
                                exception
                                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(jwtTokenFilter, AnonymousAuthenticationFilter.class)
                .build();
    }

    /**
     * Основной объект для предоставления аутентификации пользователей
     * по различным правилам. Этот метод конфигурирует и возвращает
     * объект {@link AuthenticationManager}, который используется для
     * выполнения аутентификации с применением настроенных провайдеров аутентификации.
     * <br>
     * Хранит несколько {@link AuthenticationProvider}, которые используются
     * для обработки различных типов аутентификаций.
     *
     * @param configuration Конфигурация, которая используется для получения
     *                      {@link AuthenticationManager}.
     * @return {@link AuthenticationManager}, настроенный с использованием
     * провайдеров аутентификации, определенных в конфигурации.
     * @throws Exception В случае ошибки при инициализации или получении
     *                   {@link AuthenticationManager}.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Конфигурирует и возвращает {@link AuthenticationProvider}, отвечающий за
     * аутентификацию пользователей на основе {@link UserDetailsService}.
     * Использует {@link NoOpPasswordEncoder} для обработки паролей.
     * <p>
     * Создаёт {@link DaoAuthenticationProvider} для аутентификации пользователей,
     * получаемых через {@link UserDetailsService}, и использует его с {@link AuthenticationManager}.
     *
     * @return настроенный {@link AuthenticationProvider} для аутентификации.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
