package uk.ac.city.mma.service;

import uk.ac.city.mma.model.User;
import uk.ac.city.mma.repository.UserRepository;

public class AuthService {

    private UserRepository userRepository = new UserRepository();

    public User authenticate(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }
}
