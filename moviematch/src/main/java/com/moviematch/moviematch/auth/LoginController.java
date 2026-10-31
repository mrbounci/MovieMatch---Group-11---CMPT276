package com.moviematch.moviematch.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

public class LoginController {
    @PostMapping ("/logout")
    public String logout(HttpServletRequest request) {
        LoginSession.logout(request);
        return "redirect:/login?logout";
    }
}
