package com.moviematch.moviematch.moviemodels;

import org.springframework.data.jpa.repository.JpaRepository;
import com.moviematch.moviematch.sessionmodels.SessionInformation;
import java.util.List;

public interface MovieRepository extends JpaRepository<MovieInformation, Long> {
    List<MovieInformation> findByMovieTitle(String movieTitle);
    List<MovieInformation> findByMovieTitleAndMovieRating(String movieTitle, long movieRating);
    List<MovieInformation> findBySession(SessionInformation session);
    List<MovieInformation> findBySessionOrderByMovieVoteCountDesc(SessionInformation session);
    List<MovieInformation> findBySessionOrderByMovieVoteCountDescUidAsc(SessionInformation session);
}
