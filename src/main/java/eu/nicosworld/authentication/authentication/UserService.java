package eu.nicosworld.authentication.authentication;

import eu.nicosworld.authentication.authentication.model.User;
import eu.nicosworld.authentication.exception.EmailAlreadyUsed;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String email, String password, Set<String> roles) throws EmailAlreadyUsed {
        if(userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsed();
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User loadUserByUsername(String username) {
        return userRepository.findByEmail(username).orElseThrow();
    }
}
