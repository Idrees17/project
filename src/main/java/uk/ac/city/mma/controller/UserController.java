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
}
