package com.moviematch.moviematch.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import com.moviematch.moviematch.models.users;
import com.moviematch.moviematch.models.usersrepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Runs before EVERY controller method and adds the logged-in user's info to the
 * model, so any Thymeleaf template can use:
 *   ${loggedIn}            true / false
 *   ${currentUserId}       the user's uid (Integer), or null
 *   ${currentDisplayName}  the user's display name, or null
 *
 * This is what makes role-based views possible without changing every controller.
 */
@ControllerAdvice
public class CurrentUserAdvice {

    private final usersrepository userRepo;

    public CurrentUserAdvice(usersrepository userRepo) {
        this.userRepo = userRepo;
    }

    @ModelAttribute
    public void addCurrentUser(HttpServletRequest request, Model model) {
        Integer userId = currentUserId(request);
        String displayName = null;

        if (userId != null) {
            displayName = userRepo.findById(userId)
                    .map(users::getDisplayname)
                    .orElse(null);
        }

        model.addAttribute("loggedIn", userId != null);
        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentDisplayName", displayName);
    }

    /** The logged-in user's uid, or null if nobody is logged in. */
    public static Integer currentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        // LoginSession stores the id as a Long, users.uid is an int
        Object id = session.getAttribute("userId");
        if (id instanceof Number number) {
            return number.intValue();
        }
        return null;
    }
}
