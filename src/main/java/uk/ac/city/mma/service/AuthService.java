package uk.ac.city.mma.service;

import uk.ac.city.mma.model.User;
import uk.ac.city.mma.repository.UserRepository;

public class AuthService {

    private UserRepository userRepository = new UserRepository();

    public User authenticate(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }

    /*
    Returns null on success, or an error message string if registration fails.
    */
    public String register(String username, String password, String confirmPassword) {

        if (username == null || username.isBlank()) {
            return "Username cannot be empty.";
        }

        if (password == null || password.isBlank()) {
            return "Password cannot be empty.";
        }

        if (password.length() < 6) {
            return "Password must be at least 6 characters.";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }

        if (userRepository.usernameExists(username)) {
            return "Username already taken. Please choose another.";
        }

        userRepository.createUser(username, password);
        return null;
    }
}