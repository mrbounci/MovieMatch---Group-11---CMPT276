package com.moviematch.moviematch.auth;

import com.moviematch.moviematch.models.users;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller 
public class LoginController {
    private final LoginUserRepository userRespository;
    public LoginController(LoginUserRepository userRespository) {
        this.userRespository = userRespository;
    }
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        Optional<users> foundUser = userRespository.findByUsername(username);
        if(foundUser.isEmpty()) {
            return "redirect:/login?error";
        }
        users user = foundUser.get();
        if(!PasswordVerifier.matches(password, user.getPassword())) {
            return "redirect:/login?error";
        }
        LoginSession.login(request, user.getUid(), "USER");
        return "redirect:/";
    }
    
    @PostMapping ("/logout")
    public String logout(HttpServletRequest request) {
        LoginSession.logout(request);
        return "redirect:/login?logout";
    }
}
