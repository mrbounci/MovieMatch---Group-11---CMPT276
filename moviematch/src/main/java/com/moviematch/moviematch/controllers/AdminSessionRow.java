package com.moviematch.moviematch.controllers;

import com.moviematch.moviematch.sessionmodels.SessionInformation;

/**
 * One row in the admin page's session list:
 * the session, the name of its host, and how many movies it has.
 */
public class AdminSessionRow {
    private final SessionInformation session;
    private final String hostName;
    private final int movieCount;

    public AdminSessionRow(SessionInformation session, String hostName, int movieCount) {
        this.session = session;
        this.hostName = hostName;
        this.movieCount = movieCount;
    }

    public SessionInformation getSession() {
        return session;
    }

    public String getHostName() {
        return hostName;
    }

    public int getMovieCount() {
        return movieCount;
    }
}
