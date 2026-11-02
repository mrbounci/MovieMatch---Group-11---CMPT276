package com.moviematch.moviematch.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.moviematch.moviematch.moviemodels.MovieInformation;
import com.moviematch.moviematch.moviemodels.MovieRepository;
import com.moviematch.moviematch.sessionmodels.SessionInformation;
import com.moviematch.moviematch.sessionmodels.SessionRepository;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Pages owned by the UI: login page, user dashboard, host dashboard.
 * (POST /login and POST /logout are handled by auth/LoginController.)
 */
@Controller
public class PageController {

    private final SessionRepository sessionRepo;
    private final MovieRepository movieRepo;

    public PageController(SessionRepository sessionRepo, MovieRepository movieRepo) {
        this.sessionRepo = sessionRepo;
        this.movieRepo = movieRepo;
    }

    // Home page: logged-in users go to their dashboard.
    // (LoginRequiredInterceptor already sends logged-out users to /login.)
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    // The login form. It POSTs to /login, which LoginController handles.
    // LoginController redirects back here with ?error or ?logout.
    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) {
        if (CurrentUserAdvice.currentUserId(request) != null) {
            return "redirect:/dashboard";
        }
        return "auth/login";
    }

    // USER view: every voting session the user can join.
    @GetMapping("/dashboard")
    public String userDashboard(HttpServletRequest request, Model model) {
        Integer userId = CurrentUserAdvice.currentUserId(request);
        if (userId == null) {
            return "redirect:/login";
        }

        List<SessionCard> allSessions = new ArrayList<>();
        int hostingCount = 0;
        for (SessionInformation session : sessionRepo.findAll()) {
            SessionCard card = buildCard(session, userId);
            allSessions.add(card);
            if (card.isHostedByMe()) {
                hostingCount++;
            }
        }

        model.addAttribute("sessions", allSessions);
        model.addAttribute("hostingCount", hostingCount);
        return "dashboard/user";
    }

    // HOST view: only the sessions this user created, with host controls.
    @GetMapping("/host")
    public String hostDashboard(HttpServletRequest request, Model model) {
        Integer userId = CurrentUserAdvice.currentUserId(request);
        if (userId == null) {
            return "redirect:/login";
        }

        List<SessionCard> hostedSessions = new ArrayList<>();
        int totalMovies = 0;
        for (SessionInformation session : sessionRepo.findAll()) {
            if (session.isHostedBy(userId)) {
                SessionCard card = buildCard(session, userId);
                hostedSessions.add(card);
                totalMovies += card.getMovieCount();
            }
        }

        model.addAttribute("sessions", hostedSessions);
        model.addAttribute("totalMovies", totalMovies);
        return "dashboard/host";
    }

    private SessionCard buildCard(SessionInformation session, Integer userId) {
        List<MovieInformation> movies =
                movieRepo.findBySessionOrderByMovieVoteCountDescUidAsc(session);

        String leadingMovie = null;
        int leadingVotes = 0;
        if (!movies.isEmpty() && movies.get(0).getMovieVoteCount() > 0) {
            leadingMovie = movies.get(0).getMovieTitle();
            leadingVotes = movies.get(0).getMovieVoteCount();
        }

        return new SessionCard(session, movies.size(), leadingMovie, leadingVotes,
                session.isHostedBy(userId));
    }
}
