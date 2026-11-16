package com.moviematch.moviematch.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.moviematch.moviematch.models.users;
import com.moviematch.moviematch.models.usersrepository;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The Account page: shows who you are logged in as, and lets you turn
 * admin on (with the admin code) or off for your own account.
 *
 * The admin code is NOT stored in the code since the repo is public.
 * It is read from the ADMIN_CODE environment variable, the same way as the TMDB token.
 * If ADMIN_CODE isn't set, nobody can turn admin on from this page.
 */
@Controller
public class AccountController {

    private final usersrepository userRepo;
    private final String adminCode;

    public AccountController(usersrepository userRepo, @Value("${ADMIN_CODE:}") String adminCode) {
        this.userRepo = userRepo;
        if (adminCode == null) {
            this.adminCode = "";
        } else {
            this.adminCode = adminCode.trim();
        }
    }

    @GetMapping("/account")
    public String accountPage(HttpServletRequest request, Model model) {
        users user = currentUser(request);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("account", user);
        model.addAttribute("adminCodeSet", !adminCode.isEmpty());
        return "account";
    }

    // Turns admin on for the logged-in user if they typed the right admin code.
    @PostMapping("/account/admin/on")
    public String turnAdminOn(@RequestParam(name = "code", required = false) String code,
                              HttpServletRequest request) {
        users user = currentUser(request);
        if (user == null) {
            return "redirect:/login";
        }
        if (adminCode.isEmpty() || code == null || !adminCode.equals(code.trim())) {
            return "redirect:/account?wrong";
        }
        user.setRole("ADMIN");
        userRepo.save(user);
        return "redirect:/account?on";
    }

    // Turns admin off for the logged-in user.
    @PostMapping("/account/admin/off")
    public String turnAdminOff(HttpServletRequest request) {
        users user = currentUser(request);
        if (user == null) {
            return "redirect:/login";
        }
        user.setRole("USER");
        userRepo.save(user);
        return "redirect:/account?off";
    }

    private users currentUser(HttpServletRequest request) {
        Integer userId = CurrentUserAdvice.currentUserId(request);
        if (userId == null) {
            return null;
        }
        return userRepo.findById(userId).orElse(null);
    }
}
