package com.moviematch.moviematch.reviews;

import com.moviematch.moviematch.controllers.CurrentUserAdvice;
import com.moviematch.moviematch.moviemodels.MovieInformation;
import com.moviematch.moviematch.moviemodels.MovieRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class MovieReviewController {
    private final MovieReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    public MovieReviewController(
            MovieReviewRepository reviewRepository,
            MovieRepository movieRepository) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
    }
    @PostMapping("/Asessions/{Sid}/movies/{id}/reviews")
    public String addReview(
            @PathVariable("Sid") long sessionId,
            @PathVariable("id") long movieId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String reviewText,
            HttpServletRequest request) {
        Integer userId = CurrentUserAdvice.currentUserId(request);
        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Login required");
        }
        MovieInformation movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Movie not found"));
        if (movie.getSession() == null ||
                movie.getSession().getUid() != sessionId) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Movie not in this session");
        }
        if (rating == null || rating < 1 || rating > 10) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Rating must be between 1 and 10");
        }
        if (reviewText == null ||
                reviewText.isBlank() ||
                reviewText.trim().length() > 2000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid review text");
        }
        MovieReview review = new MovieReview();
        review.setMovie(movie);
        review.setAuthorUserId(userId);
        review.setRating(rating);
        review.setReviewText(reviewText.trim());
        reviewRepository.save(review);
        return "redirect:/Asessions/" + sessionId + "/movies/" + movieId;
    }
    @GetMapping("/Asessions/{Sid}/movies/{id}/reviews")
    public String showReviews(
            @PathVariable("Sid") long sessionId,
            @PathVariable("id") long movieId,
            Model model) { 
        MovieInformation movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Movie not found"));
        if (movie.getSession() == null ||
                movie.getSession().getUid() != sessionId) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Movie not in this session");
        }
        model.addAttribute("movie", movie);
        model.addAttribute("AsessionId", sessionId);
        model.addAttribute("reviews",
                reviewRepository.findByMovieOrderByUidDesc(movie));
        return "reviews/list";
    }
    private MovieReview findOwnedReview(
            long sessionId,
            long movieId,
            long reviewId,
            HttpServletRequest request) {
            MovieReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Review not found"));
            MovieInformation movie = review.getMovie();
            if(movie == null || movie.getUid() != movieId || movie.getSession() == null ||
                movie.getSession().getUid() != sessionId) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Review not in this session/movie");
            }
            Integer userId = CurrentUserAdvice.currentUserId(request);
            if (userId == null || !userId.equals(review.getAuthorUserId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "You do not own this review");
            }
            return review;
    }
    @GetMapping("/Asessions/{Sid}/movies/{id}/reviews/{reviewId}/edit")
    public String editReview(
            @PathVariable("Sid") long sessionId,
            @PathVariable("id") long movieId,
            @PathVariable long reviewId,
            HttpServletRequest request,
            Model model) {
        MovieReview review = findOwnedReview(
                sessionId, movieId, reviewId, request);
        model.addAttribute("review", review);
        model.addAttribute("AsessionId", sessionId);
        model.addAttribute("movieId", movieId);
        return "reviews/edit";
    }
    @PostMapping("/Asessions/{Sid}/movies/{id}/reviews/{reviewId}/update")
    public String updateReview(
            @PathVariable("Sid") long sessionId,
            @PathVariable("id") long movieId,
            @PathVariable long reviewId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String reviewText,
            HttpServletRequest request) {
        MovieReview review = findOwnedReview(
                sessionId, movieId, reviewId, request);
        if (rating == null || rating < 1 || rating > 10) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Rating must be between 1 and 10");
        }
        if (reviewText == null ||
                reviewText.isBlank() ||
                reviewText.trim().length() > 2000) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid review text");
        }
        review.setRating(rating);
        review.setReviewText(reviewText.trim());
        reviewRepository.save(review);
        return "redirect:/Asessions/" + sessionId
                + "/movies/" + movieId + "/reviews";
    }
    @PostMapping("/Asessions/{Sid}/movies/{id}/reviews/{reviewId}/delete")
    public String deleteReview(
            @PathVariable("Sid") long sessionId,
            @PathVariable("id") long movieId,
            @PathVariable long reviewId,
            HttpServletRequest request) {
        MovieReview review = findOwnedReview(
                sessionId, movieId, reviewId, request);
        reviewRepository.delete(review);
        return "redirect:/Asessions/" + sessionId
                + "/movies/" + movieId + "/reviews";
    }
}
    

