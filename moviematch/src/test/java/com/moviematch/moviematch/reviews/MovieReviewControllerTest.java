package com.moviematch.moviematch.reviews;

import com.moviematch.moviematch.controllers.CurrentUserAdvice;
import com.moviematch.moviematch.moviemodels.MovieInformation;
import com.moviematch.moviematch.moviemodels.MovieRepository;
import com.moviematch.moviematch.sessionmodels.SessionInformation;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieReviewControllerTest {
    @Mock
    private MovieReviewRepository reviewRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Model model;
    @InjectMocks
    private MovieReviewController controller;

    private MovieInformation testMovie() {
        SessionInformation session = mock(SessionInformation.class);
        when(session.getUid()).thenReturn(10L);

        MovieInformation movie = new MovieInformation();
        movie.setUid(20L);
        movie.setSession(session);
        return movie;
    }
    private MovieReview testReview(MovieInformation movie, int authorId) {
        MovieReview review = new MovieReview();
        review.setUid(30L);
        review.setMovie(movie);
        review.setAuthorUserId(authorId);
        review.setRating(8);
        review.setReviewText("Good movie");
        return review;
    }
    @Test
    void validReviewIsSaved() {
        MovieInformation movie = testMovie();
        when(movieRepository.findById(20L))
                .thenReturn(Optional.of(movie));
        try (MockedStatic<CurrentUserAdvice> currentUser =
                     mockStatic(CurrentUserAdvice.class)) {
            currentUser.when(() ->
                    CurrentUserAdvice.currentUserId(request))
                    .thenReturn(1);
            String result = controller.addReview(
                    10L, 20L, 9, "Great movie", request);
            assertEquals(
                    "redirect:/Asessions/10/movies/20",
                    result);
            ArgumentCaptor<MovieReview> captor =
                    ArgumentCaptor.forClass(MovieReview.class);
            verify(reviewRepository).save(captor.capture());
            MovieReview saved = captor.getValue();
            assertEquals(1, saved.getAuthorUserId());
            assertEquals(9, saved.getRating());
            assertEquals("Great movie", saved.getReviewText());
            assertSame(movie, saved.getMovie());
        }
    }
    @Test
    void invalidRatingIsRejected() {
        MovieInformation movie = testMovie();
        when(movieRepository.findById(20L))
                .thenReturn(Optional.of(movie));
        try (MockedStatic<CurrentUserAdvice> currentUser =
                     mockStatic(CurrentUserAdvice.class)) {
            currentUser.when(() ->
                    CurrentUserAdvice.currentUserId(request))
                    .thenReturn(1);
            ResponseStatusException error = assertThrows(
                    ResponseStatusException.class,
                    () -> controller.addReview(
                            10L, 20L, 15, "Good movie", request));
            assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
            verify(reviewRepository, never()).save(any());
        }
    }
    @Test
    void emptyReviewTextIsRejected() {
        MovieInformation movie = testMovie();
        when(movieRepository.findById(20L))
                .thenReturn(Optional.of(movie));
        try (MockedStatic<CurrentUserAdvice> currentUser =
                     mockStatic(CurrentUserAdvice.class)) {
            currentUser.when(() ->
                    CurrentUserAdvice.currentUserId(request))
                    .thenReturn(1);
            ResponseStatusException error = assertThrows(
                    ResponseStatusException.class,
                    () -> controller.addReview(
                            10L, 20L, 8, "   ", request));
            assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
            verify(reviewRepository, never()).save(any());
        }
    }
    @Test
    void reviewsCanBeRetrieved() {
        MovieInformation movie = testMovie();
        MovieReview review = testReview(movie, 1);
        when(movieRepository.findById(20L))
                .thenReturn(Optional.of(movie));
        when(reviewRepository.findByMovieOrderByUidDesc(movie))
                .thenReturn(List.of(review));
        String result = controller.showReviews(10L, 20L, model);
        assertEquals("reviews/list", result);
        verify(model).addAttribute("reviews", List.of(review));
    }
    @Test
    void ownerCanEditReview() {
        MovieInformation movie = testMovie();
        MovieReview review = testReview(movie, 1);
        when(reviewRepository.findById(30L))
                .thenReturn(Optional.of(review));
        try (MockedStatic<CurrentUserAdvice> currentUser =
                     mockStatic(CurrentUserAdvice.class)) {
            currentUser.when(() ->
                    CurrentUserAdvice.currentUserId(request))
                    .thenReturn(1);
            controller.updateReview(
                    10L, 20L, 30L, 10,
                    "Updated review", request);
            assertEquals(10, review.getRating());
            assertEquals("Updated review", review.getReviewText());
            verify(reviewRepository).save(review);
        }
    }
    @Test
    void otherUserCannotEditReview() {
        MovieInformation movie = testMovie();
        MovieReview review = testReview(movie, 1);
        when(reviewRepository.findById(30L))
                .thenReturn(Optional.of(review));
        try (MockedStatic<CurrentUserAdvice> currentUser =
                     mockStatic(CurrentUserAdvice.class)) {
            currentUser.when(() ->
                    CurrentUserAdvice.currentUserId(request))
                    .thenReturn(2);
            ResponseStatusException error = assertThrows(
                    ResponseStatusException.class,
                    () -> controller.updateReview(
                            10L, 20L, 30L, 10,
                            "Changed by another user", request));
            assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
            verify(reviewRepository, never()).save(any());
        }
    }
    @Test
    void ownerCanDeleteReview() {
        MovieInformation movie = testMovie();
        MovieReview review = testReview(movie, 1);

        when(reviewRepository.findById(30L))
                .thenReturn(Optional.of(review));

        try (MockedStatic<CurrentUserAdvice> currentUser =
                     mockStatic(CurrentUserAdvice.class)) {

            currentUser.when(() ->
                    CurrentUserAdvice.currentUserId(request))
                    .thenReturn(1);

            controller.deleteReview(10L, 20L, 30L, request);

            verify(reviewRepository).delete(review);
        }
    }
    @Test
    void otherUserCannotDeleteReview() {
        MovieInformation movie = testMovie();
        MovieReview review = testReview(movie, 1);
        when(reviewRepository.findById(30L))
                .thenReturn(Optional.of(review));
        try (MockedStatic<CurrentUserAdvice> currentUser =
                     mockStatic(CurrentUserAdvice.class)) {
            currentUser.when(() ->
                    CurrentUserAdvice.currentUserId(request))
                    .thenReturn(2);
            ResponseStatusException error = assertThrows(
                    ResponseStatusException.class,
                    () -> controller.deleteReview(
                            10L, 20L, 30L, request));
            assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
            verify(reviewRepository, never()).delete(any());
        }
    }
}
