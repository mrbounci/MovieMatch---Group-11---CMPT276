
package com.moviematch.moviematch.reviews;

import com.moviematch.moviematch.moviemodels.MovieInformation;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "movie_reviews")
public class MovieReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieInformation movie;
    @Column(name = "author_user_id", nullable = false)
    private Integer authorUserId;
    @NotNull
    @Min(1)
    @Max(10)
    private Integer rating;
    @NotBlank
    @Size(max = 2000)
    @Column(length = 2000)
    private String reviewText;
    public MovieReview() {
    }
    public Long getUid() {
        return uid;
    }
    public void setUid(Long uid) {
        this.uid = uid;
    }
    public MovieInformation getMovie() {
        return movie;
    }
    public void setMovie(MovieInformation movie) {
        this.movie = movie;
    }
    public Integer getAuthorUserId() {
        return authorUserId;
    }
    public void setAuthorUserId(Integer authorUserId) {
        this.authorUserId = authorUserId;
    }
    public Integer getRating() {
        return rating;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public String getReviewText() {
        return reviewText;
    }
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}
