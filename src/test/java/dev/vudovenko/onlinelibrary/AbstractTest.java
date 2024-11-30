package dev.vudovenko.onlinelibrary;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vudovenko.onlinelibrary.users.UserRole;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import java.security.SecureRandom;

@Log4j2
@AutoConfigureMockMvc
@SpringBootTest
public class AbstractTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected UserTestUtils userTestUtils;

    protected final SecureRandom secureRandom = new SecureRandom();

    private static volatile boolean isSharedSetupDone = false;

    public static PostgreSQLContainer<?> POSTGRES_CONTAINER
            = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("root");

    static {
        log.debug("Starting shared test containers setup");
        if (!isSharedSetupDone) {
            POSTGRES_CONTAINER.start();
            isSharedSetupDone = true;
        }
    }

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("test.postgres.port", POSTGRES_CONTAINER::getFirstMappedPort);
    }

    @EventListener
    public void stopContainer(ContextStoppedEvent e) {
        POSTGRES_CONTAINER.stop();
    }

    public int getRandomInt() {
        return secureRandom.nextInt();
    }

    public String getAuthorizationHeader(UserRole role) {
        return "Bearer " + userTestUtils.getJwtTokenWithRole(role);
    }
}
