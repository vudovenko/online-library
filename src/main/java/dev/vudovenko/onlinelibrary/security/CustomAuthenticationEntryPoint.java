package dev.vudovenko.onlinelibrary.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vudovenko.onlinelibrary.web.ServerErrorDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Обработчик исключений аутентификации.
 * Формирует стандартный JSON-ответ с информацией об ошибке при неудачной аутентификации.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * Обрабатывает исключения, возникающие при неудачной аутентификации.
     * <p>
     * Данный метод вызывается, когда пользователь предоставляет невалидные
     * учетные данные или не прошёл аутентификацию. Вместо стандартной HTML-страницы
     * ошибки, он формирует JSON-ответ, содержащий описание ошибки, причину и метку времени.
     *
     * @param request       объект {@link HttpServletRequest}, представляющий HTTP-запрос.
     * @param response      объект {@link HttpServletResponse}, представляющий HTTP-ответ.
     * @param authException исключение {@link AuthenticationException}, содержащее детали ошибки аутентификации.
     * @throws IOException      если возникает ошибка ввода-вывода при записи ответа.
     * @throws ServletException если возникает ошибка на уровне сервлета.
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        log.error("Handling authentication exception", authException);
        ServerErrorDto messageResponse = new ServerErrorDto(
                "Failed to authenticate",
                authException.getMessage(),
                LocalDateTime.now()
        );

        String messageResponseJson = objectMapper.writeValueAsString(messageResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(messageResponseJson);
    }
}
