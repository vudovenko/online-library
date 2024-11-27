package dev.vudovenko.onlinelibrary.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация безопасности для приложения.
 * Этот класс настраивает аутентификацию и авторизацию пользователей в приложении,
 * а также управление сессиями и настройки безопасности HTTP.
 * <p>
 * В классе используются настройки для отключения CSRF-защиты и управления сессиями в
 * режиме "STATLESS" для API. Также настраиваются доступы к определённым URL-ам в зависимости
 * от ролей и методов запросов.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserDetailsService customUserDetailsService;

    /**
     * Метод конфигурирует фильтры безопасности для HTTP-запросов.
     * Отключает использование формы входа и CSRF-защиту, а также настраивает
     * управление сессиями в режиме "STATLESS" (без сессий).
     * <br>
     * Настроено разрешение доступа к определённым эндпоинтам, например, к методу POST
     * на /users, который доступен для всех пользователей.
     * <p>
     * Данный метод используется для конфигурации всех фильтров безопасности на уровне HTTP-запросов.
     *
     * @param http Объект для настройки фильтров безопасности HTTP.
     * @return Конфигурация фильтров безопасности для HTTP-запросов.
     * @throws Exception Если происходит ошибка при настройке фильтров безопасности.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers(HttpMethod.POST, "/authors")
                                .hasAnyAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/authors/**")
                                .hasAnyAuthority("ADMIN", "USER")
//                                .requestMatchers(HttpMethod.DELETE, "/authors/**")
//                                .hasAnyAuthority("ADMIN")

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
                                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
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
     * Конфигурирует и возвращает {@link AuthenticationProvider}, который отвечает за
     * аутентификацию пользователей на основе данных, полученных через {@link UserDetailsService}.
     * Использует {@link NoOpPasswordEncoder} для паролей.
     * <p>
     * Этот метод создаёт объект {@link DaoAuthenticationProvider}, который используется
     * для аутентификации на основе данных пользователей, получаемых через {@link UserDetailsService}.
     * <br>
     * Используется в связке с {@link AuthenticationManager} для выполнения аутентификации.
     *
     * @return {@link AuthenticationProvider}, настроенный для аутентификации через
     * {@link UserDetailsService} и {@link NoOpPasswordEncoder}.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());

        return authProvider;
    }
}
