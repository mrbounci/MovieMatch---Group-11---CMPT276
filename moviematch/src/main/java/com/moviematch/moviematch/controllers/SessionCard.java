package com.moviematch.moviematch.controllers;

import java.util.List;

import com.moviematch.moviematch.moviemodels.MovieInformation;
import com.moviematch.moviematch.sessionmodels.SessionInformation;

/**
 * What one session "card" on a dashboard needs to show.
 * Built by PageController, read by the dashboard templates.
 */
public class SessionCard {
    private final SessionInformation session;
    private final int movieCount;
    private final String leadingMovie;   // movie with the most votes, or null
    private final int leadingVotes;
    private final boolean hostedByMe;
    private final List<MovieInformation> movies;   // every movie in the session, most votes first

    public SessionCard(SessionInformation session, int movieCount,
                       String leadingMovie, int leadingVotes, boolean hostedByMe,
                       List<MovieInformation> movies) {
        this.session = session;
        this.movieCount = movieCount;
        this.leadingMovie = leadingMovie;
        this.leadingVotes = leadingVotes;
        this.hostedByMe = hostedByMe;
        this.movies = movies;
    }

    public SessionInformation getSession() {
        return session;
    }

    public int getMovieCount() {
        return movieCount;
    }

    public String getLeadingMovie() {
        return leadingMovie;
    }

    public int getLeadingVotes() {
        return leadingVotes;
    }

    public boolean isHostedByMe() {
        return hostedByMe;
    }

    public List<MovieInformation> getMovies() {
        return movies;
    }
}
