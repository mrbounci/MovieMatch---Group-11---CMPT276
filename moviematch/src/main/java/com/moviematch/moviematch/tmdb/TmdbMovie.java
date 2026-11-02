package com.moviematch.moviematch.tmdb;

import java.util.List;
import java.util.Map;

import com.moviematch.moviematch.moviemodels.GENRE;

// Holds one movie from a TMDB search.
// We only keep what the "Submit a movie" form needs: id, title, year, poster and genre.
public class TmdbMovie {

    private long id;
    private String title;
    private String year;
    private String posterUrl;
    private GENRE genre; // stays null if none of TMDB's genres match ours

    // Takes one movie from TMDB's search results and copies out the parts we need
    public static TmdbMovie fromJson(Map<String, Object> json) {
        TmdbMovie movie = new TmdbMovie();

        if (json.get("id") != null) {
            movie.id = ((Number) json.get("id")).longValue();
        }

        if (json.get("title") != null) {
            movie.title = json.get("title").toString();
        } else {
            movie.title = "";
        }

        // release_date looks like "1979-05-25" so we just take the first 4 characters
        String date = (String) json.get("release_date");
        if (date != null && date.length() >= 4) {
            movie.year = date.substring(0, 4);
        } else {
            movie.year = "";
        }

        // poster_path is just the end of the link, like "/abc123.jpg"
        // w185 means the image is 185 pixels wide
        String posterPath = (String) json.get("poster_path");
        if (posterPath != null) {
            movie.posterUrl = "https://image.tmdb.org/t/p/w185" + posterPath;
        }

        // TMDB gives a list of genre numbers, like [27, 878]
        // go through them and use the first one that matches one of our genres
        List<Object> genreIds = (List<Object>) json.get("genre_ids");
        if (genreIds != null) {
            for (int i = 0; i < genreIds.size(); i++) {
                int tmdbGenreId = ((Number) genreIds.get(i)).intValue();
                GENRE match = convertGenre(tmdbGenreId);
                if (match != null) {
                    movie.genre = match;
                    break;
                }
            }
        }

        return movie;
    }

    // Converts TMDB's genre numbers to our GENRE values.
    // TMDB's full list: https://developer.themoviedb.org/reference/genre-movie-list
    // Some TMDB genres (Animation, Family, Music...) don't have a match, so we return null.
    private static GENRE convertGenre(int tmdbGenreId) {
        switch (tmdbGenreId) {
            case 28:
                return GENRE.Action;
            case 12:
                return GENRE.Adventure;
            case 35:
                return GENRE.Comedy;
            case 80:
                return GENRE.Crime;
            case 99:
                return GENRE.Documentary;
            case 18:
                return GENRE.Drama;
            case 14:
                return GENRE.Fantasy;
            case 36:    // History
            case 10752: // War
                return GENRE.Historical;
            case 27:
                return GENRE.Horror;
            case 10749:
                return GENRE.Romance;
            case 878:
                return GENRE.SciFi;
            case 53:   // Thriller
            case 9648: // Mystery
                return GENRE.Thriller;
            default:
                return null;
        }
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public GENRE getGenre() {
        return genre;
    }
}