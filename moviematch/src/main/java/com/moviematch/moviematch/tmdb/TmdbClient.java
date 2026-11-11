package com.moviematch.moviematch.tmdb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

// Searches The Movie Database (TMDB) for movies by title.
//
// The token is NOT stored in the code since the repo is public.
// It's read from the TMDB_API_TOKEN environment variable (the long "API Read Access Token"):
//   on your PC (PowerShell):  $env:TMDB_API_TOKEN = "eyJ..."   then run the app
//   on Render:                Environment tab -> add TMDB_API_TOKEN
// If it's not set, the movie form still works, just without suggestions.
@Service
public class TmdbClient {

    private static final int MAX_RESULTS = 5;

    private final String token;
    private final RestClient http;

    public TmdbClient(@Value("${TMDB_API_TOKEN:}") String token) {
        this.token = token == null ? "" : token.trim();
        this.http = RestClient.builder()
                .baseUrl("https://api.themoviedb.org/3")
                .defaultHeader("Authorization", "Bearer " + this.token)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public boolean isConfigured() {
        return !token.isEmpty();
    }

    // returns up to 5 movies whose title matches what the user typed
    @SuppressWarnings("unchecked")
    public List<TmdbMovie> search(String title) {
        Map<String, Object> body = http.get()
                .uri(uri -> uri.path("/search/movie")
                        .queryParam("query", "{title}")   // {title} gets filled in + URL-encoded by build()
                        .queryParam("include_adult", false)
                        .queryParam("language", "en-US")
                        .queryParam("page", 1)
                        .build(title))
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        List<TmdbMovie> movies = new ArrayList<>();
        if (body != null && body.get("results") instanceof List<?> results) {
            for (Object item : results) {
                if (movies.size() == MAX_RESULTS) {
                    break;
                }
                if (item instanceof Map<?, ?> json) {
                    movies.add(TmdbMovie.fromJson((Map<String, Object>) json));
                }
            }
        }
        return movies;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> crew(long tmdbId) {
        Map<String, Object> body = http.get()
        .uri(uri -> uri.path("/movie/{id}/credits")
                .queryParam("language", "en-US")
                .build(tmdbId))
            .retrieve()
            .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Set<String>> found = new LinkedHashMap<>();
        if (body != null && body.get("crew") instanceof List<?> crew) {
            for (Object item : crew) {
            if (item instanceof Map<?, ?> person && person.get("name") != null) {
                String role = roleFor(String.valueOf(person.get("job")));
                if (role != null) {
                        found.computeIfAbsent(role, k -> new LinkedHashSet<>())
                            .add(person.get("name").toString());
                    }
                }
            }
        }

        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, Set<String>> entry : found.entrySet()) {
            result.put(entry.getKey(), String.join(", ", entry.getValue()));
        }
        return result;
    }

    private static String roleFor(String job) {
    switch (job) {
        case "Director":
            return "director";
        case "Screenplay":
        case "Writer":
            return "writer";
        case "Original Music Composer":
            return "composer";
        case "Director of Photography":
            return "cinematographer";
        default:
            return null;
    }
}
}
