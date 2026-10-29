package com.moviematch.moviematch.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.moviematch.moviematch.models.users;
import com.moviematch.moviematch.models.usersrepository;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class userscontroller {
    @Autowired
    private usersrepository userRepo;
    @GetMapping("/user-handling") 
    public String showSignupPage() {
        return "users/user-handling";
    }

    @GetMapping("/account-created")
        public String accountCreated() {
        return "users/account-created";
    }

    @PostMapping("/user-handling") 
    public String addUser(@RequestParam Map<String, String> newuser, HttpServletResponse response) {
        System.out.println("ADD user");
        String newUsername = newuser.get("username");
        String newDisplayname = newuser.get("displayname");
        String newPassword = newuser.get("password");
        userRepo.save(new users(newUsername, newDisplayname, newPassword));

        return "redirect:/account-created";
    }
}
