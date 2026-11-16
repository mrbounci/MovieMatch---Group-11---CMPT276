package com.moviematch.moviematch.moviecontrollers;


import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import com.moviematch.moviematch.moviemodels.GENRE;
import com.moviematch.moviematch.moviemodels.MovieInformation;


public class MovieForm {
    @NotBlank(message ="Title is Required")
    @Size(max = 254)
    private String movieTitle;


    @NotNull(message = "Choose a genre.")
    private GENRE movieGenre;
   

    @Size(max = 1000, message = "Optional: Describe the movie, or Reasons to vote")
    private String movieBlurb;




    @Min(value = 0)
    @Max(value = 10)
    private int movieRating = 0;


    private int movieVoteCount = 0;
   
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    private Long tmdbMovieId;

    private String moviePosterUrl; 

    private String movieYear;

    private String movieSynopsis;

    private String movieDirector;

    private String movieWriter;

    private String movieComposer;

    private String movieCinematographer;

    public static MovieForm from(MovieInformation movie) {
        MovieForm form = new MovieForm();
        form.setMovieTitle(movie.getMovieTitle());
        form.setMovieGenre(movie.getMovieGenre());
        form.setMovieBlurb(movie.getMovieBlurb());
        form.setMovieRating(movie.getMovieRating());
        form.setMovieCount(movie.getMovieVoteCount());
        form.setTmdbMovieId(movie.getTmdbMovieId());
        form.setMoviePosterUrl(movie.getMoviePosterUrl()); 
        form.setMovieYear(movie.getMovieYear());
        form.setMovieSynopsis(movie.getMovieSynopsis());
        return form;
    }


    public void applyTo(MovieInformation movie) {
        movie.setMovieTitle(movieTitle);
        movie.setMovieGenre(movieGenre);
        movie.setMovieBlurb(movieBlurb);
        movie.setMovieRating(movieRating);
        movie.setTmdbMovieId(tmdbMovieId);
        movie.setMoviePosterUrl(moviePosterUrl);
        movie.setMovieYear(movieYear);
        movie.setMovieSynopsis(movieSynopsis);
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





