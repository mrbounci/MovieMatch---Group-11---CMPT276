package com.moviematch.moviematch.tmdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
}
