package eu.nicosworld.falloutback.authentication;

import eu.nicosworld.falloutback.authentication.model.User;
import eu.nicosworld.falloutback.exception.EmailAlreadyUsed;
import eu.nicosworld.falloutback.infrastructure.event.UserCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = applicationEventPublisher;
    }

    public User registerUser(String email, String password, Set<String> roles)
            throws EmailAlreadyUsed {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsed();
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        eventPublisher.publishEvent(new UserCreatedEvent(this, savedUser));

        return savedUser;
    }

    public User loadUserByUsername(String username) {
        return userRepository.findByEmail(username).orElseThrow();
    }
}
