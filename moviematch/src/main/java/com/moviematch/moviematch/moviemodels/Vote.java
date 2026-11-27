package com.moviematch.moviematch.moviemodels;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import jakarta.persistence.Id;

@Entity
@Table(name = "movie_vote", uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "user_id"}))
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    public Vote() {} 

    public Vote(Long uid, Long sessionId, Long userId, Long movieId) {
        this.uid = uid;
        this.sessionId = sessionId;
        this.userId = userId;
        this.movieId = movieId;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
}  