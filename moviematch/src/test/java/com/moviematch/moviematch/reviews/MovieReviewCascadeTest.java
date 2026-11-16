package com.moviematch.moviematch.reviews;

import com.moviematch.moviematch.moviemodels.GENRE;
import com.moviematch.moviematch.moviemodels.MovieInformation;
import com.moviematch.moviematch.moviemodels.MovieRepository;
import com.moviematch.moviematch.sessionmodels.SessionInformation;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.ANY
)
@ContextConfiguration(
        classes = MovieReviewCascadeTest.JpaOnlyConfiguration.class
)
class MovieReviewCascadeTest {
    @SpringBootConfiguration
    @EntityScan(basePackageClasses = {
            MovieInformation.class,
            MovieReview.class,
            SessionInformation.class
    })
    @EnableJpaRepositories(basePackageClasses = {
            MovieRepository.class,
            MovieReviewRepository.class
    })
    static class JpaOnlyConfiguration {
    }
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieReviewRepository reviewRepository;
    @Autowired
    private EntityManager entityManager;
    @Test
    void deletingMovieRemovesItsReviewsButNotOtherReviews() {
        MovieInformation movie1 = new MovieInformation(
                "Test Movie 1", "Test", 8, GENRE.values()[0], 0);
        MovieInformation movie2 = new MovieInformation(
                "Test Movie 2", "Test", 8, GENRE.values()[0], 0);
        movie1 = movieRepository.saveAndFlush(movie1);
        movie2 = movieRepository.saveAndFlush(movie2);
        MovieReview review1 = new MovieReview();
        review1.setMovie(movie1);
        review1.setAuthorUserId(1);
        review1.setRating(9);
        review1.setReviewText("Review for movie 1");
        review1 = reviewRepository.saveAndFlush(review1);
        MovieReview review2 = new MovieReview();
        review2.setMovie(movie2);
        review2.setAuthorUserId(2);
        review2.setRating(7);
        review2.setReviewText("Review for movie 2");
        review2 = reviewRepository.saveAndFlush(review2);

        long movie1Id = movie1.getUid();
        long movie2Id = movie2.getUid();
        long review1Id = review1.getUid();
        long review2Id = review2.getUid();
        entityManager.flush();
        entityManager.clear();
        MovieInformation movieToDelete = movieRepository.findById(movie1Id)
                .orElseThrow();
        movieRepository.delete(movieToDelete);
        movieRepository.flush();
        entityManager.clear();
        
        assertFalse(movieRepository.existsById(movie1Id));
        assertFalse(reviewRepository.existsById(review1Id));

        assertTrue(movieRepository.existsById(movie2Id));
        assertTrue(reviewRepository.existsById(review2Id));
    }
}
