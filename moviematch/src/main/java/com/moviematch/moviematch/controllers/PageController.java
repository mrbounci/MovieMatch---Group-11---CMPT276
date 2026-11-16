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
import com.moviematch.moviematch.tmdb.TmdbClient;
import com.moviematch.moviematch.tmdb.TmdbMovie;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Pages owned by the UI: login page, home page, user dashboard, host dashboard.
 * (POST /login and POST /logout are handled by auth/LoginController.)
 */
@Controller
public class PageController {

    private final SessionRepository sessionRepo;
    private final MovieRepository movieRepo;
    private final TmdbClient tmdb;

    public PageController(SessionRepository sessionRepo, MovieRepository movieRepo, TmdbClient tmdb) {
        this.sessionRepo = sessionRepo;
        this.movieRepo = movieRepo;
        this.tmdb = tmdb;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    // Home page: a short guide to the site, the user's own numbers,
    // and a row of movies that are popular on TMDB right now.
    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model) {
        Integer userId = CurrentUserAdvice.currentUserId(request);
        if (userId == null) {
            return "redirect:/login";
        }

        int sessionCount = 0;
        int hostingCount = 0;
        for (SessionInformation session : sessionRepo.findAll()) {
            sessionCount++;
            if (session.isHostedBy(userId)) {
                hostingCount++;
            }
        }

        // If TMDB is not set up or is down, the home page still works, just without this row.
        List<TmdbMovie> popular = new ArrayList<>();
        if (tmdb.isConfigured()) {
            try {
                popular = tmdb.popular();
            } catch (Exception e) {
                popular = new ArrayList<>();
            }
        }

        model.addAttribute("sessionCount", sessionCount);
        model.addAttribute("hostingCount", hostingCount);
        model.addAttribute("popular", popular);
        return "home";
    }

    // The login form. It POSTs to /login, which LoginController handles.
    // LoginController redirects back here with ?error or ?logout.
    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) {
        if (CurrentUserAdvice.currentUserId(request) != null) {
            return "redirect:/home";
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
                session.isHostedBy(userId), movies);
    }
}
