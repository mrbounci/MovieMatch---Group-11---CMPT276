package com.moviematch.moviematch.moviemodels;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface MovieVoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findBySessionIdAndUserId(Long sessionId, Long userId);
    List<Vote> findBySessionId(Long sessionId);
}
