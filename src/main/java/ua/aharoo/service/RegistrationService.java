package ua.aharoo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.aharoo.model.User;
import ua.aharoo.registration.email.EmailService;
import ua.aharoo.registration.email.token.ConfirmationToken;
import ua.aharoo.registration.email.token.ConfirmationTokenService;
import ua.aharoo.util.UserRole;

import java.time.LocalDateTime;

@Service
public class RegistrationService {

    private final ConfirmationTokenService confirmationTokenService;
    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public RegistrationService(ConfirmationTokenService confirmationTokenService, UserService userService, EmailService emailService) {
        this.confirmationTokenService = confirmationTokenService;
        this.userService = userService;
        this.emailService = emailService;
    }

    public String register(User user){

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setEmail(user.getEmail());
        newUser.setRole(UserRole.USER);

        String token = userService.signUpUser(newUser);

        String link = "http://localhost:8080/registration/confirm?token=" + token;

        emailService.send(user.getEmail(),emailService.buildEmail(user.getUsername(),link));

        return token;
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.enableUser(
                confirmationToken.getUser().getEmail());
        return "confirmed";
    }
}
