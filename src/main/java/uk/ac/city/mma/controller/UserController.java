package uk.ac.city.mma.controller;

import uk.ac.city.mma.model.User;
import uk.ac.city.mma.service.AuthService;

public class UserController {

    private AuthService authService = new AuthService();

    public String login(String username, String password) {

        User user = authService.authenticate(username, password);

        if (user == null) {
            return "login-failed";
        }

        if (user.getRole() == User.Role.ADMIN) {
            return "admin-dashboard";
        } else {
            return "member-dashboard";
        }
    }

    public User authenticateUser(String username, String password) {
        return authService.authenticate(username, password);
    }

    /*
    Returns null on success, or an error message if registration fails.
    */
    public String register(String username, String password, String confirmPassword) {
        return authService.register(username, password, confirmPassword);
    }
}
