package com.moviematch.moviematch.reviews;

import com.moviematch.moviematch.moviemodels.MovieInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovieReviewRepository
        extends JpaRepository<MovieReview, Long> {
    List<MovieReview> findByMovieOrderByUidDesc(MovieInformation movie);

}
