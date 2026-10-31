package com.moviematch.moviematch.moviemodels;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<MovieInformation, Long> {
    List<MovieInformation> findByMovieTitle(String movieTitle);
    List<MovieInformation> findByMovieTitleAndMovieRating(String movieTitle, long movieRating);
}
