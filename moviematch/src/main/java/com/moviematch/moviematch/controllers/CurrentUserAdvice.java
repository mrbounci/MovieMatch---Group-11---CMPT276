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
 *   ${currentPath}         the current URL path, e.g. /dashboard
 *   ${isAdmin}             true if the logged-in user is an admin
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
        boolean admin = false;

        if (userId != null) {
            HttpSession session = request.getSession(false);
            users user = userRepo.findById(userId).orElse(null);

            if (user == null) {
                // the account was deleted while they were logged in, so log them out
                session.invalidate();
                userId = null;
            } else {
                displayName = user.getDisplayname();
                admin = user.isAdmin();
                // keep the role saved in the login session up to date, so making
                // or removing an admin takes effect on that person's next click
                session.setAttribute("userRole", user.getRole());
            }
        }

        model.addAttribute("loggedIn", userId != null);
        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentDisplayName", displayName);
        // the nav uses this to highlight the tab for the page you are on
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("isAdmin", admin);
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

    /**
     * True if the logged-in user is an admin.
     * Reads the role from the login session, which addCurrentUser() refreshes
     * from the database at the start of every request.
     */
    public static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        return "ADMIN".equals(session.getAttribute("userRole"));
    }
}
