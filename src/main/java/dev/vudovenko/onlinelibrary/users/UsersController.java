package dev.vudovenko.onlinelibrary.users;

import dev.vudovenko.onlinelibrary.security.jwt.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @RequestBody @Valid SignUpRequest signUpRequest
    ) {
        log.info("Get request for sign-up: login={}", signUpRequest.login());
        User user = userService.registerUser(signUpRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserDto(user.id(), user.login()));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse> authenticate(
            @RequestBody @Valid SignInRequest signInRequest
    ) {
        log.info("Get request for sign-in: login={}", signInRequest.login());

        String token = authenticationService.authenticateUser(signInRequest);

        return ResponseEntity.ok(new JwtTokenResponse(token));
    }
}
