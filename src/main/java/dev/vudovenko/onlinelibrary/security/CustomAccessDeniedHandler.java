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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Обработчик исключений отказа в доступе.
 * <p>
 * Этот класс вызывается, когда аутентифицированный пользователь пытается получить доступ
 * к ресурсу, для которого у него нет необходимых прав. Он формирует стандартный JSON-ответ
 * с информацией об ошибке.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * Обрабатывает исключения отказа в доступе, отправляя JSON-ответ с деталями ошибки.
     *
     * @param request               объект {@link HttpServletRequest}.
     * @param response              объект {@link HttpServletResponse}.
     * @param accessDeniedException исключение, возникающее при отказе в доступе.
     * @throws IOException      если возникает ошибка ввода-вывода при обработке исключения.
     * @throws ServletException если происходит ошибка на уровне сервлета.
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        log.error("Handling access denied exception", accessDeniedException);
        ServerErrorDto messageResponse = new ServerErrorDto(
                "Forbidden",
                accessDeniedException.getMessage(),
                LocalDateTime.now()
        );

        String messageResponseJson = objectMapper.writeValueAsString(messageResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(messageResponseJson);
    }
}
