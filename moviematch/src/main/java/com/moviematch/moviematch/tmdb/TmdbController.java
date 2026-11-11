package com.moviematch.moviematch.tmdb;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moviematch.moviematch.controllers.CurrentUserAdvice;

import jakarta.servlet.http.HttpServletRequest;

// Used by static/js/movie-lookup.js on the "Submit a movie" form.
// GET /api/tmdb/search?q=alien  ->  [ {id, title, year, posterUrl, genre}, ... ]
// Returns an empty list instead of an error so the form keeps working if TMDB is down.
@RestController
public class TmdbController {

    private final TmdbClient tmdb;

    public TmdbController(TmdbClient tmdb) {
        this.tmdb = tmdb;
    }

    /*@GetMapping("/api/tmdb/search")
    public List<TmdbMovie> search(@RequestParam(name = "q", required = false) String q,
                                  HttpServletRequest request) {
        // only logged-in users, and don't bother searching for 1 letter
        if (CurrentUserAdvice.currentUserId(request) == null || q == null || q.trim().length() < 2) {
            return List.of();
        }
        if (!tmdb.isConfigured()) {
            return List.of();
        }
        try {
            return tmdb.search(q.trim());
        } catch (RuntimeException e) {
            System.out.println("TMDB search failed: " + e.getMessage());
            return List.of();
        }
    }*/
   @GetMapping("/api/tmdb/search")
    public List<TmdbMovie> search(@RequestParam(name = "q", required = false) String q,
                              HttpServletRequest request) {
    if (CurrentUserAdvice.currentUserId(request) == null) {
        System.out.println("TMDB: no logged-in user, returning empty");
        return List.of();
    }
    if (q == null || q.trim().length() < 2) {
        return List.of();
    }
    if (!tmdb.isConfigured()) {
        System.out.println("TMDB: TMDB_API_TOKEN is not set");
        return List.of();
    }
    try {
        return tmdb.search(q.trim());
    } catch (org.springframework.web.client.RestClientResponseException e) {
        System.out.println("TMDB HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString());
        return List.of();
    } catch (RuntimeException e) {
        e.printStackTrace();
        return List.of();
    }
}
}
