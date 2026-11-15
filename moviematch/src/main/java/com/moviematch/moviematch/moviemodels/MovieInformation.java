package com.moviematch.moviematch.moviemodels;

import com.moviematch.moviematch.sessionmodels.SessionInformation;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;




@Entity
@Table(name="movieInformation")
public class MovieInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @NotBlank(message ="Title is Required")
    @Size(max = 1000)
    private String movieTitle;

    @Size(max = 1000, message = "Optional: Describe the movie, or Reasons to vote")
    private String movieBlurb;

    @Column(name = "movie_year", length = 4)
    private String movieYear;

    @Column(name = "movie_synopsis", length = 5000)
    private String movieSynopsis;

    @Column(name = "movie_director", length = 100)
    private String movieDirector;

    @Column(name = "movie_writer", length = 100)
    private String movieWriter;

    @Column(name = "movie_composer", length = 100)
    private String movieComposer;

    @Column(name = "movie_cinematographer", length = 100)
    private String movieCinematographer;

    @Min(1)
    @Max(10)
    private int movieRating;

    @NotNull
    @Enumerated(EnumType.STRING)
    private GENRE movieGenre;


    private int movieVoteCount;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private SessionInformation session;

    private String moviePosterUrl; 

    
    public MovieInformation(){

    }
    public MovieInformation(String movieTitle, String movieBlurb, int movieRating, GENRE movieGenre, int movieVoteCount) {
        this.movieTitle = movieTitle;
        this.movieBlurb = movieBlurb;
        this.movieRating = movieRating; 
        this.movieGenre = movieGenre; 
        this.movieVoteCount = movieVoteCount;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieBlurb() {
        return movieBlurb;
    }

    public void setMovieBlurb(String movieBlurb) {
        this.movieBlurb = movieBlurb;
    }

    public int getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(int movieRating) {
        this.movieRating = movieRating;
    }

    public int getMovieVoteCount() {
        return movieVoteCount;
    }

    public void setMovieCount(int movieVoteCount) {
        this.movieVoteCount = movieVoteCount;
    }

    public GENRE getMovieGenre() {
        return movieGenre;
    }

    public void setMovieGenre(GENRE genre) {
        this.movieGenre = genre;
    }

    public String getRoleTitle() {
        return GenreConstructor.create(movieGenre).displayTitle();
    }

    public SessionInformation getSession() {
        return session;
    }

    public void setSession(SessionInformation session) {
        this.session = session;
    }

    @Column(name = "tmdb_movie_id")
    private Long tmdbMovieId;

    public Long getTmdbMovieId() {
        return tmdbMovieId;
    }

    public void setTmdbMovieId(Long tmdbMovieId) {
        this.tmdbMovieId = tmdbMovieId;
    }

    public String getMoviePosterUrl() {
        return this.moviePosterUrl;
    }

    public void setMoviePosterUrl(String moviePosterUrl) {
        this.moviePosterUrl = moviePosterUrl;
    }

    public String getMovieYear() {
        return this.movieYear;
    }

    public void setMovieYear(String movieYear) {
        this.movieYear = movieYear;
    }

    public String getMovieSynopsis() {
        return this.movieSynopsis;
    }

    public void setMovieSynopsis(String movieSynopsis) {
        this.movieSynopsis = movieSynopsis;
    }

    public String getMovieDirector() {
        return this.movieDirector;
    }

    public void setMovieDirector(String movieDirector) {
        this.movieDirector = movieDirector;
    }

    public String getMovieWriter() {
        return this.movieWriter;
    }

    public void setMovieWriter(String movieWriter) {
        this.movieWriter = movieWriter;
    }

    public String getMovieComposer() {
        return this.movieComposer;
    }

    public void setMovieComposer(String movieComposer) {
        this.movieComposer = movieComposer;
    }

    public String getMovieCinematographer() {
        return this.movieCinematographer;
    }

    public void setMovieCinematographer(String movieCinematographer) {
        this.movieCinematographer = movieCinematographer;
    }
}
