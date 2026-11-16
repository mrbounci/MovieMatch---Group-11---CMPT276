package com.moviematch.moviematch.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import com.moviematch.moviematch.models.users;
import com.moviematch.moviematch.models.usersrepository;
import com.moviematch.moviematch.moviemodels.MovieRepository;
import com.moviematch.moviematch.sessionmodels.SessionInformation;
import com.moviematch.moviematch.sessionmodels.SessionRepository;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The Admin tab. An admin can:
 *   - see every account, make someone an admin, take admin away, or delete the account
 *   - see every session and open, edit or delete it
 * Admins also pass the host checks in sessionController and movieController,
 * so they can edit or delete any session or movie and reset any session's votes.
 */
@Controller
public class AdminController {

    private final usersrepository userRepo;
    private final SessionRepository sessionRepo;
    private final MovieRepository movieRepo;

    public AdminController(usersrepository userRepo, SessionRepository sessionRepo,
                           MovieRepository movieRepo) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
        this.movieRepo = movieRepo;
    }

    @GetMapping("/admin")
    public String adminPage(HttpServletRequest request, Model model) {
        if (CurrentUserAdvice.currentUserId(request) == null) {
            return "redirect:/login";
        }
        checkIsAdmin(request);

        // sorted by id so the rows stay in the same order after a change
        List<users> allUsers = userRepo.findAll(Sort.by("uid"));
        int adminCount = 0;
        for (users user : allUsers) {
            if (user.isAdmin()) {
                adminCount++;
            }
        }

        List<AdminSessionRow> sessionRows = new ArrayList<>();
        for (SessionInformation session : sessionRepo.findAll(Sort.by("uid"))) {
            String hostName = "No host";
            if (session.getHostId() != null) {
                users host = userRepo.findById(session.getHostId()).orElse(null);
                if (host == null) {
                    hostName = "Deleted user";
                } else {
                    hostName = host.getDisplayname();
                }
            }
            int movieCount = movieRepo.findBySession(session).size();
            sessionRows.add(new AdminSessionRow(session, hostName, movieCount));
        }

        model.addAttribute("users", allUsers);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("sessionRows", sessionRows);
        return "admin/index";
    }

    @PostMapping("/admin/users/{id}/make-admin")
    public String makeAdmin(@PathVariable int id, HttpServletRequest request) {
        checkIsAdmin(request);
        users user = findUser(id);
        user.setRole("ADMIN");
        userRepo.save(user);
        return "redirect:/admin";
    }

    @PostMapping("/admin/users/{id}/remove-admin")
    public String removeAdmin(@PathVariable int id, HttpServletRequest request) {
        checkIsAdmin(request);
        users user = findUser(id);
        user.setRole("USER");
        userRepo.save(user);

        // if you just took admin away from yourself you can't see /admin any more
        Integer myId = CurrentUserAdvice.currentUserId(request);
        if (myId != null && myId == id) {
            return "redirect:/account?off";
        }
        return "redirect:/admin";
    }

    // Deletes an account. Sessions they hosted stay (shown as "Deleted user")
    // so an admin can decide whether to keep or delete each one.
    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable int id, HttpServletRequest request) {
        checkIsAdmin(request);
        Integer myId = CurrentUserAdvice.currentUserId(request);
        if (myId != null && myId == id) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't delete your own account");
        }
        users user = findUser(id);
        userRepo.delete(user);
        return "redirect:/admin";
    }

    // Deletes a session together with its movies.
    @PostMapping("/admin/sessions/{id}/delete")
    @Transactional
    public String deleteSession(@PathVariable long id, HttpServletRequest request) {
        checkIsAdmin(request);
        SessionInformation session = sessionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
        movieRepo.deleteBySessionUid(session.getUid());
        sessionRepo.delete(session);
        return "redirect:/admin";
    }

    private users findUser(int id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    // stops anyone who isn't an admin, even if they type the URL in directly
    private void checkIsAdmin(HttpServletRequest request) {
        if (!CurrentUserAdvice.isAdmin(request)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admins only");
        }
    }
}
